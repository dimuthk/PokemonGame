package src.board

import actors.PlayerActor
import akka.actor.ActorRef
import akka.event.{EventBus, LookupClassification}

case class MessageEvent(val channel: String, val message: String)

class StateEventBus extends EventBus with LookupClassification {

  type Event = MessageEvent
  type Classifier = String
  type Subscriber = PlayerActor

  override protected def mapSize() : Int = 1

  override protected def classify(event : Event) : Classifier = event.channel

  override protected def publish(event : Event, subscriber : Subscriber) : Unit = {
    subscriber.sendMessageToClient(event)
  }

  override protected def compareSubscribers(a : Subscriber, b : Subscriber) : Int = a.compareTo(b)

}
