package src.board

import actors.PlayerActor

import scala.util.Random

import src.card.Placeholder
import src.json.Identifier
import src.board.intermediary.IntermediaryRequest
import src.board.state.StateGeneratorDirector
import src.board.BoardCleaner
import src.board.move.MoveDirector
import src.board.drag._
import src.board.state.DefaultStateGenerator
import src.card.Card
import src.card.energy._
import src.card.pokemon._
import src.card.pokemon.base_set._
import src.card.pokemon.jungle._
import src.move.Move
import src.move.PokemonPower
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
        p.deck = List.fill(20)(new Squirtle()) ++ List.fill(20)(new WaterEnergy())
        p.deck = Random.shuffle(p.deck)
        p.active = Some(new Venusaur())
        p.isTurn = false
      } else {
        p.deck = List.fill(20)(new WaterEnergy()) ++ List.fill(20)(new Machop()) ++ List.fill(20)(new GrassEnergy())
        p.deck = Random.shuffle(p.deck)
        p.isTurn = true
      }

      distributeInitialCards()
      ready = true
      if (bothCorrespondentsReady) {
        broadcastState(
          DefaultStateGenerator.generateForPlayer1(c1.get.p, c2.get.p),
          DefaultStateGenerator.generateForPlayer2(c1.get.p, c2.get.p))
      }
    }

    def rebroadcastState() {
      broadcastState(
          DefaultStateGenerator.generateForPlayer1(c1.get.p, c2.get.p),
          DefaultStateGenerator.generateForPlayer2(c1.get.p, c2.get.p))
    }

    def handleMove(contents : Seq[String]) {
      val intermediary = MoveDirector.handleMoveCommand(p, getOpponent(p), contents)
      if (intermediary.isDefined) {
        rebroadcastStateWithIntermediary(intermediary.get)
      } else {
          cleanupBoardAndPassBackState()
      }
    }

    def handleDrag(contents : Seq[String]) {
      val intermediary = DragDirector.handleDragCommand(p, getOpponent(p), contents)
      if (intermediary.isDefined) {
        Logger.debug("intermediary: " + intermediary.get)
        rebroadcastStateWithIntermediary(intermediary.get)
      } else {
         cleanupBoardAndPassBackState()
      }
    }

    def rebroadcastStateWithIntermediary(intermediary : IntermediaryRequest) {
      val state = StateGeneratorDirector.generateState(c1.get.p, c2.get.p, isPlayer1(p))
      broadcastState(state._1, state._2, Some(intermediary))
    }

    def cleanupBoardAndPassBackState() {
      BoardCleaner.cleanUpBoardState(p, getOpponent(p))
      val state = StateGeneratorDirector.generateState(c1.get.p, c2.get.p, isPlayer1(p))
      broadcastState(state._1, state._2)
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

  def isPlayer1(p : Player) : Boolean = return c1.get.p == p

  def flipTurn() : Unit = {
    c1.get.p.isTurn = !c1.get.p.isTurn
    c2.get.p.isTurn = !c2.get.p.isTurn
  }

  def bothCorrespondentsReady =
      !c1.isEmpty && !c2.isEmpty && c1.get.ready && c2.get.ready

  def getIntermediaryForPlayer(p : Player, intermediary : Option[IntermediaryRequest]) : JsObject = {
    if (intermediary.isDefined) {
      if (intermediary.get.p == p) {
        return intermediary.get.toJson
      }
    }
    return Placeholder.toJson
  }

  def broadcastState(
    p1Orientation : (JsObject, JsObject),
    p2Orientation : (JsObject, JsObject),
    intermediary : Option[IntermediaryRequest] = None) {
    eventBus.publish(StateEvent(
      1, JsObject(Seq(
        "player1" -> p1Orientation._1,
        "player2" -> p1Orientation._2,
        //"intermediary" -> JsString("INTERMEDIARY")))))
        "intermediary" -> getIntermediaryForPlayer(c1.get.p, intermediary)))))
    eventBus.publish(StateEvent(
      2, JsObject(Seq(
        "player1" -> p2Orientation._1,
        "player2" -> p2Orientation._2,
        //"intermediary" -> JsString("INTERMEDIARY")))))
        "intermediary" -> getIntermediaryForPlayer(c2.get.p, intermediary)))))
    c1.get.p.notification = None
    c2.get.p.notification = None
  }

}
