import akka.actor.ActorRef
import akka.event.EventBus

class StateEventBus extends EventBus {

  type Event = String
  type Classifier = String
  type Subscriber = ActorRef

  override protected def classify(event : Event) : Classifier = event

  override protected def publish(event : Event, subscriber : Subscriber) : Unit = {
 subscriber ! event
  }

  override protected def compareSubscribers(a : Subscriber, b : Subscriber) : Int = a.compareTo(b)

}
