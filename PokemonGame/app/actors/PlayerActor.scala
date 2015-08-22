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
        case "HAND_TO_ACTIVE" => correspondent.handToActive(contents(1).split("Hand")(1).toInt)
        case "HAND_TO_BENCH" => correspondent.handToBench(contents(1).split("Hand")(1).toInt, contents(2).split("Bench")(1).toInt - 1)
        case "ATTACK" => correspondent.attack(if (contents(1) == "one") 1 else 2)
      }
    }
    case StateEvent(_, state) => out ! state.toString
    case _ => ()
  }

  def compareTo(other : PlayerActor) : Int = correspondent.id - other.correspondent.id

}
