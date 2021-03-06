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
      contents.head match {
        case "DRAG" => correspondent.handleDrag(contents.tail)
        case "MOVE" => correspondent.handleMove(contents.tail)
        case "UPDATE" => correspondent.handleUpdate(contents.tail)
        case "FLIP" => contents(1) match {
          case "DRAG" => correspondent.other.handleDrag(contents.tail.tail)
          case "MOVE" => correspondent.other.handleMove(contents.tail.tail)
          case "UPDATE" => correspondent.handleUpdate(contents.tail.tail)
        }
      }
    }
    case StateEvent(_, state) => out ! state.toString
    case _ => ()
  }

  def compareTo(other : PlayerActor) : Int = correspondent.id - other.correspondent.id

}
