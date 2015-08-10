package actors

import akka.actor._

object PlayerActor {
  def props(out : ActorRef, id : Int) = Props(new PlayerActor(out, id))
}

class PlayerActor(out : ActorRef, id : Int) extends Actor {
  def receive = {
    case msg: String =>
      out ! ("I received your message: " + msg + ". You are client " + id)
  }
}
