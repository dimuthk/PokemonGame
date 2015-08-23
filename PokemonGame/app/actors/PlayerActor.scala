package actors

import src.board.AddChild
import src.board.Board.Correspondent
import src.board.BoardCommand
import src.board.StateCommand
import src.board.StateEvent
import src.board.StateEventBus
import play.api.Logger
import akka.actor._

object PlayerActor {
  def props(out : ActorRef, correspondent : Correspondent) =
      Props(new PlayerActor(out, correspondent))
}

class PlayerActor(out : ActorRef, var correspondent : Correspondent) extends Actor {

  val BUILD_MACHOP = "BUILD_MACHOP"

  override def postStop = {
    correspondent.eventBus.unsubscribe(this, correspondent.id)
    correspondent.terminateSocket()
    correspondent = null
  }

  override def preStart = {
    correspondent.eventBus.subscribe(this, correspondent.id)
  }

  def receive = {
    case BUILD_MACHOP => correspondent.populateMachop
    case m : String => {
      Logger.debug("Incoming message: " + m)
      val contents = m.split("<>")
      contents(0) match {
        case "ACTIVE_TO_ACTIVE" => correspondent.rebroadcastState()
        case "ACTIVE_TO_BENCH" => correspondent.handleMove(correspondent.activeToBench, Map(
            "drop" -> (contents(1).toInt - 1)),
            "ACTIVE_TO_BENCH")
        case "BENCH_TO_ACTIVE" => correspondent.handleMove(correspondent.benchToActive, Map(
            "drag" -> (contents(1).toInt - 1)),
            "BENCH_TO_ACTIVE")
        case "BENCH_TO_BENCH" => correspondent.handleMove(correspondent.benchToBench, Map(
            "drag" -> (contents(1).toInt - 1),
            "drop" -> (contents(2).toInt - 1)),
            "BENCH_TO_BENCH")
        case "HAND_TO_ACTIVE" => correspondent.handleMove(correspondent.handToActive, Map(
            "drag" -> (contents(1).toInt)),
            "HAND_TO_ACTIVE")
        case "HAND_TO_BENCH" => correspondent.handleMove(correspondent.handToBench, Map(
            "drag" -> (contents(1).toInt),
            "drop" -> (contents(2).toInt - 1)),
            "HAND_TO_BENCH")
        case "ATTACK_FROM_ACTIVE" => correspondent.attackFromActive(contents(1).toInt)
        case "ATTACK_FROM_BENCH" => correspondent.attackFromBench(contents(2).toInt, contents(1).toInt - 1)
      }
    }
    case StateEvent(_, state) => out ! state.toString
    case _ => ()
  }

  def compareTo(other : PlayerActor) : Int = correspondent.id - other.correspondent.id

}
