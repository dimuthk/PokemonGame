package src.board

import actors.PlayerActor
import akka.actor.ActorRef
import akka.actor._
import play.api.libs.json._

case class BoardCommand(cmd : String)

case class StateCommand(state : String)

case class AddChild(actorRef : ActorRef)

class Board {

  val eventBus = new StateEventBus()
  var cnt = 0

  def canAccept() : Boolean = {
    return cnt < 2
  }

  def forkPlayerActor(out : ActorRef) : Props = {
    val p = PlayerActor.props(out, this, cnt)
    cnt += 1
    return p
  }

  def onTerminateSocket() = {
    cnt -= 1
  }

  def echo(m : String) {
    eventBus.publish(MessageEvent("state", m))
  }

}
