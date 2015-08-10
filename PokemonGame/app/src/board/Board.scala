package src.board

import actors.PlayerActor
import akka.actor.ActorRef
import akka.actor._

class SuperVisor extends Actor {

  def receive = {
    case p : Props => context.actorOf(p, "p")
    case _ => ()
  }

}

class Board {

  val system = ActorSystem("system")
  val superVisor = system.actorOf(Props[SuperVisor], "superVisor")

  val eventBus : StateEventBus = new StateEventBus()

  def processMessage(message : String) : Unit = {
    eventBus.publish(MessageEvent("playerChannel", message))
  }

  def forkPlayerActor(out : ActorRef) : Props = {
    val p = Props(new PlayerActor(out))
    superVisor ! p
    return p
  }

  def registerActor(actor : PlayerActor) : Unit = {
    eventBus.subscribe(actor, "playerChannel")
  }

}
