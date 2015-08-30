package src.player

import src.card.pokemon.EvolvedPokemon
import scala.util.Random
import src.card.CardUI
import src.card.Card
import src.move._
import src.card.Placeholder
import src.card.pokemon.PokemonCard
import src.json.Identifier
import src.json.Jsonable

import play.api.Logger
import play.api.libs.json._

class Player extends Jsonable {

  private var _deck : Seq[Card] = List()

  def deck = _deck

  def setDeck(cards : Seq[Card]) = _deck = cards

  def shuffleDeck() : Unit = _deck = Random.shuffle(_deck)

  def dealCardsToHand(count : Int) {
    _hand = _hand ++ _deck.slice(0, count)
    _deck = _deck.slice(count + 1, _deck.length)
  }

  def dealPrizeCards() {
    for (i <- 0 until 6) {
      prizes(i) = Some(_deck(i))
    }
    _deck = _deck.slice(7, _deck.length)
  }
  
  private var _hand : Seq[Card] = List()

  def hand = _hand

  var garbage : Seq[Card] = List()

  private val _bench : Array[Option[PokemonCard]] = Array.fill(5) { None }

  def bench = _bench

  val prizes : Array[Option[Card]] = Array.fill(6) { None }

  private var _active : Option[PokemonCard] = None

  def active = _active

  var notification : Option[String] = None

  var isTurn : Boolean = false

  var addedEnergy : Boolean = false

  def ownsMove(m : Move) : Boolean = existingActiveAndBenchCards.filter(_.ownsMove(m)).length > 0

  def existingActiveAndBenchCards : Seq[PokemonCard] = return (bench.toList ++ List(active)).flatten

  def moveActiveToHand() {
    if (active.isDefined && !active.get.stuck) {
      _hand = _hand ++ active.get.pickUp()
    }
  }

  def swapActiveAndBench(benchIndex : Int) {
    if (_active.isDefined && !_active.get.stuck) {
      val tmp = _active
      _active = _bench(benchIndex)
      _bench(benchIndex) = tmp
    }
  }

  def swapBenchCards(benchIndex1 : Int, benchIndex2 : Int) {
    val tmp = _bench(benchIndex1)
    _bench(benchIndex1) = _bench(benchIndex2)
    _bench(benchIndex2) = tmp
  }

  def evolveActiveCard(handIndex : Int) {
    hand(handIndex) match {
      case ep : EvolvedPokemon => {
        if (ep.isEvolutionOf(active.get)) {
          ep.evolveOver(active.get)
          _active = Some(ep)
          removeCardFromHand(handIndex)
        }
      }
      case _ => ()
    }
  }

  def evolveBenchCard(handIndex : Int, benchIndex : Int) {
    hand(handIndex) match {
      case ep : EvolvedPokemon => {
        if (ep.isEvolutionOf(bench(benchIndex).get)) {
          ep.evolveOver(bench(benchIndex).get)
          _bench(benchIndex) = Some(ep)
          removeCardFromHand(handIndex)
        }
      }
      case _ => ()
    }
  }

  def moveHandToActive(handIndex : Int) {
    if (active.isDefined) {
      throw new Exception("Tried to move hand card to non-empty active slot")
    }
    hand(handIndex) match {
      case pc : PokemonCard => {
        _active = Some(pc)
        removeCardFromHand(handIndex)
      }
      case _ => ()
    }
  }

  def moveHandToBench(handIndex : Int, benchIndex : Int) {
    if (bench(benchIndex).isDefined) {
      throw new Exception("Tried to move hand card to non-empty bench slot")
    }
    hand(handIndex) match {
      case pc : PokemonCard => {
        _bench(benchIndex) = Some(pc)
        removeCardFromHand(handIndex)
      }
    }
  }

  def setUIOrientationForBench(uiSet : Set[CardUI.Value]) {
    for (bc : Option[PokemonCard] <- bench) {
      if (bc.isDefined) {
        bc.get.setUiOrientation(uiSet)
      }
    }
  }

  def cardWithActivatedPower : Option[PokemonCard] = existingActiveAndBenchCards.find {
    case ap : ActivePokemonPower => ap.isActive
    case _ => false
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
    _hand = _hand.slice(0, handIndex) ++ _hand.slice(handIndex + 1, _hand.size)
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