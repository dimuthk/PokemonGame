package actors

import src.board.Board
import src.board.MessageEvent
import src.board.SuperVisor
import akka.actor._

class PlayerActor(out : ActorRef) extends Actor {

  //board.registerActor(this)

  def receive = {
    case msg : String => 
      //board.processMessage(msg)
      context.parent ! msg
      //out ! ("I received your message: " + msg)
    //case msgEvent : MessageEvent =>
    //  out ! ("I received from message via eventBus: " + msgEvent.message)
  }

  def sendMessageToClient(msgEvent : MessageEvent) : Unit = {
    out ! ("I received your message via eventBus: " + msgEvent.message)
  }

  // Shouldn't matter
  def compareTo(other : PlayerActor) : Int = 0

}
