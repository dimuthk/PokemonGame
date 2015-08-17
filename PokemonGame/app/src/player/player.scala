package src.player

import src.card.Card
import src.card.Placeholder
import src.card.pokemon.PokemonCard
import src.json.Identifier
import src.json.Jsonable

import play.api.libs.json._
import scala.reflect.ClassTag
import scala.collection.mutable.MutableList

class Player extends Jsonable {

  var deck : Seq[Card] = List()
  var hand : Seq[Card] = List()
  var garbage : Seq[Card] = List()

  val bench : Array[Option[PokemonCard]] = Array.fill(5) { None }
  val prizes : Array[Option[Card]] = Array.fill(6) { None }

  var active : Option[PokemonCard] = None

  private def optionCardToJson[T <: Card : ClassTag](oc : Option[T]) : JsObject = oc match {
  	case Some(c) => c.toJson
  	case None => Placeholder.toJson
  }

  private def listToJsArray(list : Seq[Card]) : JsArray = {
  	return list.foldLeft(new JsArray())((curr, c) => curr.prepend(c.toJson))
  }

  private def optionListToJsArray[T <: Card : ClassTag](list : Seq[Option[T]]) : JsArray = {
    return list.foldLeft(new JsArray())((curr, c) => curr.prepend(optionCardToJson(c)))
  }

  override def toJsonImpl = Json.obj(
  	Identifier.ACTIVE.toString -> optionCardToJson(active),
  	Identifier.DECK.toString -> listToJsArray(deck),
  	Identifier.HAND.toString -> listToJsArray(hand),
  	Identifier.GARBAGE.toString -> listToJsArray(garbage),
  	Identifier.BENCH.toString -> optionListToJsArray[PokemonCard](bench),
  	Identifier.PRIZES.toString -> optionListToJsArray(prizes))

  override def getIdentifier = Identifier.PLAYER

}