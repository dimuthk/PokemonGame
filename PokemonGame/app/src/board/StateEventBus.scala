package src.board

import actors.PlayerActor
import akka.actor._
import akka.actor.ActorRef
import akka.event.{EventBus, LookupClassification}

import play.api.libs.json._

case class StateEvent(val channel : Integer, val state: JsValue)

class StateEventBus extends EventBus with LookupClassification {

  type Event = StateEvent
  type Classifier = Integer
  type Subscriber = PlayerActor

  override protected def mapSize() : Int = 1

  override protected def classify(event : Event) : Classifier = event.channel

  override protected def publish(event : Event, subscriber : Subscriber) : Unit = {
    subscriber.self ! event
  }

  override protected def compareSubscribers(a : Subscriber, b : Subscriber) : Int = a.compareTo(b)

}
