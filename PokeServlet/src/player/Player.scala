package player

import card.Card
import card.Placeholder
import card.pokemon.PokemonCard
import json.Jsonable
import json.JSONIdentifier
import json.JSONSchema

import org.json.JSONArray
import org.json.JSONObject

class Player extends Jsonable {
  
  val deck : Seq[Card] = List()
  val hand : Seq[Card] = List()
  val garbage : Seq[Card] = List()
  
  val bench : Array[Option[PokemonCard]] = Array.fill(5){None}
  val prizes : Array[Option[Card]] = Array.fill(6){None}
  
  var active : Option[PokemonCard] = None
  
  def addCardToJsonArr(curr : JSONArray, c : Card) = curr.put(c)
  def addMaybeCardToJsonArr(curr : JSONArray, oc : Option[Card]) = curr.put(optionCardToJson(oc))
  
  def optionCardToJson(oc : Option[Card]) : JSONObject = oc match {
    case Some(c) => c.toJson()
    case None => Placeholder.toJson()
  }
  
  override def toJsonImpl() = new JSONObject()
      .put("active", optionCardToJson(active))
      .put("deck", deck.foldLeft(new JSONArray())(addCardToJsonArr))
      .put("hand", hand.foldLeft(new JSONArray())(addCardToJsonArr))
      .put("garbage", garbage.foldLeft(new JSONArray())(addCardToJsonArr))
      .put("bench", bench.foldLeft(new JSONArray())(addMaybeCardToJsonArr))
      .put("prizes", prizes.foldLeft(new JSONArray())(addMaybeCardToJsonArr))

  override def setJsonValues(json : JSONObject) {
    
  }
  
  override def getIdentifier() = JSONIdentifier.PLAYER
  
}