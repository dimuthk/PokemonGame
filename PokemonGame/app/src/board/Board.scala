package src.board

import actors.PlayerActor

import scala.util.Random

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
        p.deck = List.fill(20)(new Caterpie()) ++ List.fill(20)(new Venusaur()) ++ List.fill(20)(new GrassEnergy())
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

    def attackUsing(pc : PokemonCard, moveNum : Int) {
      val move = if (moveNum == 1) pc.firstMove.get else pc.secondMove.get
      move.perform(p, getOpponent(p))
      move match {
        case power : PokemonPower => ()
        case _ => flipTurn()
      }
      updateMoveStatuses()
      val ui = interceptedUI()
      if (ui.isDefined) {
        broadcastState(ui.get._1, ui.get._2)
      } else {
        broadcastState(
          DefaultStateGenerator.generateForPlayer1(c1.get.p, c2.get.p),
          DefaultStateGenerator.generateForPlayer2(c1.get.p, c2.get.p))
      }
    }

    def attackFromActive(moveNum : Int) {
      Logger.debug("attack " + moveNum)
      attackUsing(p.active.get, moveNum)
    }

    def attackFromBench(moveNum : Int, benchIndex : Int) {
      attackUsing(p.bench(benchIndex).get, moveNum)
    }

    def handleDrag(move : DragCommand) {
      DefaultDragInterpreter.handleDrag(p, move)
      updateMoveStatuses()
      val ui = interceptedUI()
      if (ui.isDefined) {
        broadcastState(ui.get._1, ui.get._2)
      } else {
        broadcastState(
          DefaultStateGenerator.generateForPlayer1(c1.get.p, c2.get.p),
          DefaultStateGenerator.generateForPlayer2(c1.get.p, c2.get.p))
      }
    }

    def interceptedUI() : Option[((JsObject, JsObject), (JsObject, JsObject))] = {
      for (pc : Option[PokemonCard] <- p.bench ++ List(p.active)) {
        if (pc.isDefined) {
          for (om : Option[Move] <- List(pc.get.firstMove, pc.get.secondMove)) {
            if (om.isDefined && om.get.stateGenerator.isDefined) {
              val generator = om.get.stateGenerator.get
              if (generator.isActive) {
                val p1 = if (isPlayer1(p)) p else getOpponent(p)
                val p2 = if (isPlayer1(p)) getOpponent(p) else p
                return Some(generator.generateForPlayer1(p1, p2, pc.get), generator.generateForPlayer2(p1, p2, pc.get))
              }
            }
          }
        }
      }
      return None
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
    for (p : Player <- List[Player](c1.get.p, c2.get.p)) {
      if (p.active.isDefined) {
        val active = p.active.get
        for (m <- List(p.active.get.firstMove, p.active.get.secondMove)) {
          if (m.isDefined) {
              m.get match {
                case power : PokemonPower => {
                  if (active.statusCondition.isEmpty) {
                    if (power.isActivatable) {
                      power.status = if (power.activated) Status.ACTIVATED else Status.ACTIVATABLE
                    } else {
                      power.status = Status.PASSIVE
                    }
                  } else {
                    power.status = Status.DISABLED
                  }
                }
                 // Active pokemon with non-activatable moves should be enabled if there's enough energy.
                case move : Move => move.status =
                  if (move.hasEnoughEnergy(p, active.energyCards)) Status.ENABLED else Status.DISABLED
              }
          }
        }
      }

      for (pc : Option[PokemonCard] <- p.bench) {
        if (pc.isDefined) {
          for (m <- List[Option[Move]](pc.get.firstMove, pc.get.secondMove)) {
            if (m.isDefined) {
              m.get match {
                  case power : PokemonPower => power.status = Status.PASSIVE
                  case move : Move => move.status = Status.DISABLED
              }
            }
          }
        }
      }
    }
  }

  def isPlayer1(p : Player) : Boolean = return c1.get.p == p

  def flipTurn() : Unit = {
    c1.get.p.isTurn = !c1.get.p.isTurn
    c2.get.p.isTurn = !c2.get.p.isTurn
  }

  def bothCorrespondentsReady =
      !c1.isEmpty && !c2.isEmpty && c1.get.ready && c2.get.ready

  def broadcastState(p1Orientation : (JsObject, JsObject), p2Orientation : (JsObject, JsObject)) {
    eventBus.publish(StateEvent(
      1, JsObject(Seq("player1" -> p1Orientation._1, "player2" -> p1Orientation._2))))
    eventBus.publish(StateEvent(
      2, JsObject(Seq("player1" -> p2Orientation._1, "player2" -> p2Orientation._2))))
    c1.get.p.notification = None
    c2.get.p.notification = None
  }

}
