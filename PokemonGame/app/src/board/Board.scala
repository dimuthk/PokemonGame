package src.board

import actors.PlayerActor


import src.card.Placeholder
import src.json.Identifier
import src.board.intermediary.IntermediaryRequest
import src.board.state.StateGeneratorDirector
import src.board.update.UpdateDirector
import src.board.move.MoveDirector
import src.board.drag._
import src.board.state.DefaultStateGenerator
import src.card.Card
import src.card.energy._
import src.card.pokemon._
import src.card.pokemon.base_set._
import src.card.pokemon.jungle._
import src.card.pokemon.fossil._
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
        p.setDeck(List.fill(20)(new Rattata()))
        p.shuffleDeck()
        p.setActive(new Electrode())
      } else {
        p.setDeck(List.fill(20)(new Machop()))
        p.shuffleDeck()
        p.flipTurn()
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

    def other : Correspondent = if (c1.get == this) c2.get else c1.get

    def handleMove(args : Seq[String]) : Unit =
      MoveDirector.handleCommand(p, getOpponent(p), args) match {
        case Some(intermediary) => passBackState(Some(intermediary))
        case None => handleUpdate(List("", "1"))
    }

    def handleDrag(args : Seq[String]) : Unit =
      DragDirector.handleCommand(p, getOpponent(p), args) match {
        case Some(intermediary) => passBackState(Some(intermediary))
        case None => handleUpdate(List("", "1"))
    }

    def handleUpdate(args : Seq[String]) : Unit =
      UpdateDirector.handleCommand(p, getOpponent(p), args) match {
        case Some(intermediary) => passBackState(Some(intermediary))
        case None => passBackState()
    }

    def passBackState(maybeIntermediary : Option[IntermediaryRequest] = None) {
      val state = StateGeneratorDirector.generateState(c1.get.p, c2.get.p, isPlayer1(p))
      broadcastState(state._1, state._2, maybeIntermediary)
    }

    /**
     * Takes deck and distributes original cards appropriately.
     */
    def distributeInitialCards() : Unit = {
      p.dealPrizeCards()
      p.dealCardsToHand(7)
      Logger.debug("init hand: " + p.hand)
    }

    def terminateSocket() = onTerminateSocket(id)

  }

  def isPlayer1(p : Player) : Boolean = return c1.get.p == p

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
