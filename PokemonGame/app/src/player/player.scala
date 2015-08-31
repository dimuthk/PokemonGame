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

  var trainerBan : Boolean = false

  def deck = _deck

  def setDeck(cards : Seq[Card]) = _deck = cards

  def shuffleDeck() : Unit = _deck = Random.shuffle(_deck)

  def dealCardsToHand(count : Int) {
    _hand = _hand ++ _deck.slice(0, count)
    _deck = _deck.slice(count, _deck.length)
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

  def benchIsFull = _bench.length == _bench.toList.flatten.length

  val prizes : Array[Option[Card]] = Array.fill(6) { None }

  private var _active : Option[PokemonCard] = None

  def setActive(pc : PokemonCard) {
    _active = Some(pc)
  }

  def active = _active

  var notification : Option[String] = None

  var isTurn : Boolean = false

  var addedEnergy : Boolean = false

  def ownsMove(m : Move) : Boolean = existingActiveAndBenchCards.filter(_.ownsMove(m)).length > 0

  def existingActiveAndBenchCards : Seq[PokemonCard] = return (List(active) ++ bench.toList).flatten

  def moveActiveToHand() {
    if (active.isDefined && !active.get.agility) {
      _hand = _hand ++ active.get.pickUp()
      _active = None
    }
  }

  def swapActiveAndBench(benchIndex : Int) {
    if (_active.isDefined && _active.get.agility) {
      return
    }
    val tmp = _active
    _active = _bench(benchIndex)
    _bench(benchIndex) = tmp
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

  def moveDeckToBench(deckIndex : Int, benchIndex : Int) : Unit = _deck(deckIndex) match {
    case pc : PokemonCard => {
      _bench(benchIndex) = Some(pc)
      removeCardFromDeck(deckIndex)
    }
    case _ => throw new Exception("Tried to move a non-pokemon card from deck to bench")
  }

  def removeCardFromDeck(deckIndex : Int) {
    _deck = _deck.slice(0, deckIndex) ++ _deck.slice(deckIndex + 1, _deck.size)
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

  def customJson(
    activeMoves : Option[JsArray] = None,
    benchMoves : Option[JsArray] = None,
    handMoves : Option[JsObject] = None) : JsObject = {
    val notifyJson = notification match {
      case Some(s) => JsString(s)
      case None => Placeholder.toJson
    }
    return Json.obj(
       Identifier.ACTIVE.toString -> (active match {
        case Some(a) => activeMoves match {
          case Some(am) => a.customMoveJson(Some(am))
          case None => optionCardToJson(active)
        }
        case None => Placeholder.toJson
       }),
       Identifier.DECK.toString -> cardListToJsArray(deck),
       Identifier.HAND.toString -> (handMoves match {
        case Some(m) => m
        case None => cardListToJsArray(hand)
       }),
       Identifier.GARBAGE.toString -> cardListToJsArray(garbage),
       Identifier.BENCH.toString -> (benchMoves match {
        case Some(m) => benchForCustomJson(m)
        case None => optionCardListToJsArray[PokemonCard](bench)
       }),
       Identifier.PRIZES.toString -> optionCardListToJsArray(prizes),
       Identifier.NOTIFICATION.toString -> notifyJson,
       Identifier.IS_TURN.toString -> JsBoolean(isTurn))
  }

  def benchForCustomJson(benchMoves : JsArray) : JsArray = {
    return bench.foldRight(new JsArray())((c, curr) => curr.prepend(customMoveJson(c, benchMoves)))
  }

  def customMoveJson(pc : Option[PokemonCard], benchMoves : JsArray) : JsObject = pc match {
    case Some(c) => c.customMoveJson((Some(benchMoves)))
    case None => Placeholder.toJson
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