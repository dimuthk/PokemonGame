package actors

import src.board._
import src.board.drag._
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
        case "ACTIVE_TO_BENCH" => correspondent.handleDrag(ActiveToBench(contents(1).toInt - 1))
        case "BENCH_TO_ACTIVE" => correspondent.handleDrag(BenchToActive(contents(1).toInt - 1))
        case "BENCH_TO_BENCH" => correspondent.handleDrag(BenchToBench(contents(1).toInt - 1, contents(2).toInt - 1))
        case "HAND_TO_ACTIVE" => correspondent.handleDrag(HandToActive(contents(1).toInt))
        case "HAND_TO_BENCH" => correspondent.handleDrag(HandToBench(contents(1).toInt, contents(2).toInt - 1))
        case "ATTACK_FROM_ACTIVE" => correspondent.attackFromActive(contents(1).toInt)
        case "ATTACK_FROM_BENCH" => correspondent.attackFromBench(contents(2).toInt, contents(1).toInt - 1)
      }
    }
    case StateEvent(_, state) => out ! state.toString
    case _ => ()
  }

  def compareTo(other : PlayerActor) : Int = correspondent.id - other.correspondent.id

}
