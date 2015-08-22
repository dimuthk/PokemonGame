package src.board

import actors.PlayerActor

import scala.util.Random

import src.card.Card
import src.card.energy._
import src.card.pokemon._
import src.player.Player

import play.api.Logger
import play.api.libs.json._

import akka.actor.ActorRef
import akka.actor._
import play.api.libs.json._

case class BoardCommand(cmd : String)

case class StateCommand(state : String)

case class AddChild(actorRef : ActorRef)

object Board {

  val eventBus = new StateEventBus()

  var c1 : Option[Correspondent] = None
  var c2 : Option[Correspondent] = None

  def canAccept : Boolean = c1.isEmpty || c2.isEmpty

  def forkPlayerActor(out : ActorRef) : Props = {
    if (c1.isEmpty) {
      c1 = Some(new Correspondent(eventBus, 1))
      return PlayerActor.props(out, c1.get)
    } else if (c2.isEmpty) {
      c2 = Some(new Correspondent(eventBus, 2))
      return PlayerActor.props(out, c2.get)
    }
    throw new Exception("No correspondents available")
  }

  def onTerminateSocket(id : Int) = {
    if (id == 1) {
      c1 = None
    } else if (id == 2) {
      c2 = None
    }
  }

  def getOpponent(p : Player) : Player = return if (p == c1.get.p) c2.get.p else c1.get.p

  class Correspondent(val eventBus : StateEventBus, val id : Int) {

    val p : Player = new Player()

    /**
     * Signals that the deck has been established for this correspondent.
     * Gameplay will not begin until this has happened
     */
    var ready = false

    def populateMachop : Unit = {
      if (id == 1) {
        p.deck = List.fill(30)(new Machop()) ++ List.fill(30)(new FireEnergy())
        p.deck = Random.shuffle(p.deck)
      } else {
        p.deck = List.fill(30)(new Rattata()) ++ List.fill(30)(new WaterEnergy())
        p.deck = Random.shuffle(p.deck)
      }

      distributeInitialCards()
      ready = true
      if (bothCorrespondentsReady) {
        broadcastState()
      }
    }

    def attack(moveNum : Int) {
      Logger.debug("attack " + moveNum)
      val activeCard = p.active.get
      if (moveNum == 1) {
        activeCard.firstMove.get.perform(p, getOpponent(p))
      }
      broadcastState()
    }

    def handToActive(handIndex : Int) {
      val card : Card = p.hand(handIndex)
      if (p.active.isEmpty) {
        card match {
          case pc : PokemonCard => {
            p.active = Some(pc)
            p.hand = p.hand.slice(0, handIndex) ++ p.hand.slice(handIndex + 1, p.hand.size)
          }
          case _ => ()
        }
      } else {
        val active = p.active.get
        card match {
          case ec : EnergyCard => {
            active.energyCards = active.energyCards ++ List(ec)
            p.hand = p.hand.slice(0, handIndex) ++ p.hand.slice(handIndex + 1, p.hand.size)
          }
          case _ => ()
        }
      }

      broadcastState()
    }

    def handToBench(handIndex : Int, benchIndex : Int) {
      Logger.debug("handToBench(" + handIndex + ", " + benchIndex + ")")
      val card : Card = p.hand(handIndex)
      if (p.bench(benchIndex).isEmpty) {
        card match {
          case pc : PokemonCard => {
            p.bench(benchIndex) = Some(pc)
            p.hand = p.hand.slice(0, handIndex) ++ p.hand.slice(handIndex + 1, p.hand.size)
          }
          case _ => ()
        }
      } else {
        val benchCard = p.bench(benchIndex).get
        card match {
          case ec : EnergyCard => {
            benchCard.energyCards = benchCard.energyCards ++ List(ec)
            p.hand = p.hand.slice(0, handIndex) ++ p.hand.slice(handIndex + 1, p.hand.size)
          }
          case _ => ()
        }
      }
      broadcastState()
    }

    /**
     * Takes deck and distributes original cards appropriately.
     */
    def distributeInitialCards() : Unit = {
      for (i <- 0 until 6) {
        p.prizes(i) = Some(p.deck(i))
      }
      p.deck = p.deck.slice(6, 60)
      p.hand = p.deck.slice(0, 7)
      Logger.debug("init hand: " + p.hand)
      p.deck = p.deck.slice(7, 54)
    }

    def terminateSocket() = onTerminateSocket(id)

  }

  def bothCorrespondentsReady =
      !c1.isEmpty && !c2.isEmpty && c1.get.ready && c2.get.ready


  def broadcastState() {
    eventBus.publish(StateEvent(
      1, JsObject(Seq("player1" -> c1.get.p.toJson, "player2" -> c2.get.p.toJson))))
    eventBus.publish(StateEvent(
      2, JsObject(Seq("player1" -> c2.get.p.toJson, "player2" -> c1.get.p.toJson))))
  }

}
