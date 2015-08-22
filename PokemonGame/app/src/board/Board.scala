package src.board

import actors.PlayerActor

import scala.util.Random

import src.card.Card
import src.card.energy._
import src.card.pokemon._
import src.move.Move
import src.move.Status
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
        p.deck = List.fill(20)(new Bulbasaur()) ++ List.fill(20)(new GrassEnergy()) ++ List.fill(20)(new Venusaur())
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
      val move = if (moveNum == 1) activeCard.firstMove.get else activeCard.secondMove.get
      if (move.isActivatable) {
        move.status match {
          case Status.ACTIVATABLE => move.status = Status.ACTIVATED
          case Status.ACTIVATED => move.status = Status.ACTIVATABLE
          case _ => ()
        }
      } else {
        move.perform(p, getOpponent(p))
      }
      updateMoveStatuses()
      broadcastState()
    }

    def handToActive(handIndex : Int) {
      val card : Card = p.hand(handIndex)
      if (p.active.isEmpty) {
        card match {
          // Moving basic pokemon from hand to active slot.
          case pc : PokemonCard => {
            if (pc.evolutionStage == EvolutionStage.BASIC) {
              p.active = Some(pc)
              p.hand = p.hand.slice(0, handIndex) ++ p.hand.slice(handIndex + 1, p.hand.size)
            }
          }
          case _ => ()
        }
      } else {
        val active = p.active.get
        card match {
          // Attaching energy card to active pokemon.
          case ec : EnergyCard => {
            active.energyCards = active.energyCards ++ List(ec)
            p.hand = p.hand.slice(0, handIndex) ++ p.hand.slice(handIndex + 1, p.hand.size)
          }
          // Evolving active pokemon.
          case pc : PokemonCard => {
            if (pc.isEvolutionOf(active)) {
              pc.preEvolution = Some(active)
              p.active = Some(pc)
              p.hand = p.hand.slice(0, handIndex) ++ p.hand.slice(handIndex + 1, p.hand.size)
            }
          }
          case _ => ()
        }
      }

      updateMoveStatuses()
      broadcastState()
    }

    def handToBench(handIndex : Int, benchIndex : Int) {
      Logger.debug("handToBench(" + handIndex + ", " + benchIndex + ")")
      val card : Card = p.hand(handIndex)
      if (p.bench(benchIndex).isEmpty) {
        card match {
          case pc : PokemonCard => {
            // Moving basic pokemon to empty bench slot.
            if (pc.evolutionStage == EvolutionStage.BASIC) {
              p.bench(benchIndex) = Some(pc)
              p.hand = p.hand.slice(0, handIndex) ++ p.hand.slice(handIndex + 1, p.hand.size)
            }
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
          // Evolving this bench pokemon.
          case pc : PokemonCard => {
            if (pc.isEvolutionOf(benchCard)) {
              pc.preEvolution = Some(benchCard)
              p.bench(benchIndex) = Some(pc)
              p.hand = p.hand.slice(0, handIndex) ++ p.hand.slice(handIndex + 1, p.hand.size)
            }
          }
          case _ => ()
        }
      }
      updateMoveStatuses()
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

  def updateMoveStatuses() {
    Logger.debug("CORRESPONDENTS: " + c1 + ", " + c2)
    for (p : Player <- List[Player](c1.get.p, c2.get.p)) {
    //List(c1.get.p, c2.get.p).foreach {
      if (p.active.isDefined) {
        val active = p.active.get
        for (m <- List(p.active.get.firstMove, p.active.get.secondMove)) {
          if (m.isDefined) {
            if (m.get.isActivatable) {
              if (m.get.status != Status.ACTIVATED && active.statusCondition.isEmpty) {
                m.get.status = Status.ACTIVATABLE
              }
            } else {
            // Active pokemon with non-activatable moves should be enabled if there's enough energy.
              m.get.status =
                  if (m.get.hasEnoughEnergy(active.energyCards)) Status.ENABLED else Status.DISABLED
            }
          }
        }
      }

      for (pc : Option[PokemonCard] <- p.bench) {
        if (pc.isDefined) {
          for (m <- List[Option[Move]](pc.get.firstMove, pc.get.secondMove)) {
            if (m.isDefined) {
              if (m.get.isActivatable) {
                if (m.get.status != Status.ACTIVATED && pc.get.statusCondition.isEmpty) {
                  m.get.status = Status.ACTIVATABLE
                }
              } else {
              // Benched pokemon cannot use non-activatable moves.
                m.get.status = Status.DISABLED
              }
            }
          }
        }
      }
    }
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
