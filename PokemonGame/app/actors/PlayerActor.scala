package actors

import src.board.AddChild
import src.board.Board.Correspondent
import src.board.BoardCommand
import src.board.StateCommand
import src.board.StateEvent
import src.board.StateEventBus
import akka.actor._

object PlayerActor {
  def props(out : ActorRef, correspondent : Correspondent) =
      Props(new PlayerActor(out, correspondent))
}

class PlayerActor(out : ActorRef, var correspondent : Correspondent) extends Actor {

  val BUILD_MACHOP = "BUILD_MACHOP"
  val HAND_TO_ACTIVE = "HAND_TO_ACTIVE"

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
      val contents = m.split("<>")
      contents(0) match {
        case HAND_TO_ACTIVE => correspondent.handToActive(contents(1).split("Hand")(1).toInt)
      }
    }
    case StateEvent(_, state) => out ! state.toString
    case _ => ()
  }

  def compareTo(other : PlayerActor) : Int = correspondent.id - other.correspondent.id

}
