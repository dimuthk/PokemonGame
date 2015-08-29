package src.player

import src.card.CardUI
import src.card.Card
import src.move.Move
import src.card.Placeholder
import src.card.pokemon.PokemonCard
import src.json.Identifier
import src.json.Jsonable

import play.api.Logger
import play.api.libs.json._

class Player extends Jsonable {

  var deck : Seq[Card] = List()
  var hand : Seq[Card] = List()
  var garbage : Seq[Card] = List()

  val bench : Array[Option[PokemonCard]] = Array.fill(5) { None }
  val prizes : Array[Option[Card]] = Array.fill(6) { None }

  var active : Option[PokemonCard] = None

  var notification : Option[String] = None

  var isTurn : Boolean = false

  var addedEnergy : Boolean = false

  def ownsMove(m : Move) : Boolean = existingActiveAndBenchCards.filter(_.ownsMove(m)).length > 0

  def existingActiveAndBenchCards : Seq[PokemonCard] = return (bench.toList ++ List(active)).flatten

  def setUIOrientationForBench(uiSet : Set[CardUI.Value]) {
    for (bc : Option[PokemonCard] <- bench) {
      if (bc.isDefined) {
        bc.get.setUiOrientation(uiSet)
      }
    }
  }

  def setUIOrientationForActiveAndBench(uiSet : Set[CardUI.Value]) {
    if (active.isDefined) {
      active.get.setUiOrientation(uiSet)
    }
    setUIOrientationForBench(uiSet)
  }

  def setUiOrientationForHand(uiSet : Set[CardUI.Value]) {
    for (hc : Card <- hand) {
      hc.setUiOrientation(uiSet)
    }
  }

  def notify(msg : String) {
    notification = Some(msg)
  }

  def removeCardFromHand(handIndex : Int) {
    hand = hand.slice(0, handIndex) ++ hand.slice(handIndex + 1, hand.size)
  }

  override def toJsonImpl : JsObject = {
    val notifyJson = notification match {
      case Some(s) => JsString(s)
      case None => Placeholder.toJson
    }
    return Json.obj(
  	   Identifier.ACTIVE.toString -> optionCardToJson(active),
  	   Identifier.DECK.toString -> cardListToJsArray(deck),
  	   Identifier.HAND.toString -> cardListToJsArray(hand),
  	   Identifier.GARBAGE.toString -> cardListToJsArray(garbage),
  	   Identifier.BENCH.toString -> optionCardListToJsArray[PokemonCard](bench),
  	   Identifier.PRIZES.toString -> optionCardListToJsArray(prizes),
       Identifier.NOTIFICATION.toString -> notifyJson,
       Identifier.IS_TURN.toString -> JsBoolean(isTurn))
  }

  override def getIdentifier = Identifier.PLAYER

}
