package actors

import akka.actor._

object MyWebSocketActor {
  def props(out : ActorRef, id : Int) = Props(new MyWebSocketActor(out, id))
}

class MyWebSocketActor(out : ActorRef, id : Int) extends Actor {
  def receive = {
    case msg: String =>
      out ! ("I received your message: " + msg + ". You are client " + id)
  }
}
