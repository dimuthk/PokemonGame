package actors

import src.board.AddChild
import src.board.Board
import src.board.BoardCommand
import src.board.StateCommand
import src.board.MessageEvent
import src.board.StateEventBus
import akka.actor._

object PlayerActor {
  def props(out : ActorRef, board : Board, id : Int) =
      Props(new PlayerActor(out, board, id))
}

class PlayerActor(out : ActorRef, var board : Board, val id : Int) extends Actor {
  
  override def postStop = {
    board.eventBus.unsubscribe(this, "state")
    board.onTerminateSocket()
    board = null
  }

  override def preStart = {
    board.eventBus.subscribe(this, "state")
  }

  def receive = {
    case m : String => {
      board.echo(m)
    }
    case MessageEvent("state", m) => out ! ("From hq: " + m)
    case _ => ()
  }

  def sendMessageToClient(msgEvent : MessageEvent) : Unit = {
    out ! ("I received your message via eventBus: " + msgEvent.message)
  }

}
