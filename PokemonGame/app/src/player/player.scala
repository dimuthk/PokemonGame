package src.player

import src.card.Card
import src.card.Placeholder
import src.card.pokemon.PokemonCard
import src.json.Identifier
import src.json.Jsonable

import play.api.libs.json._

class Player extends Jsonable {

  var deck : Seq[Card] = List()
  var hand : Seq[Card] = List()
  var garbage : Seq[Card] = List()

  val bench : Array[Option[PokemonCard]] = Array.fill(5) { None }
  val prizes : Array[Option[Card]] = Array.fill(6) { None }

  var active : Option[PokemonCard] = None

  override def toJsonImpl = Json.obj(
  	Identifier.ACTIVE.toString -> optionCardToJson(active),
  	Identifier.DECK.toString -> cardListToJsArray(deck),
  	Identifier.HAND.toString -> cardListToJsArray(hand),
  	Identifier.GARBAGE.toString -> cardListToJsArray(garbage),
  	Identifier.BENCH.toString -> optionCardListToJsArray[PokemonCard](bench),
  	Identifier.PRIZES.toString -> optionCardListToJsArray(prizes))

  override def getIdentifier = Identifier.PLAYER

}
