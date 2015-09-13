package src.player

import src.card.pokemon.EvolvedPokemon
import scala.util.Random
import src.card.CardUI
import src.card.Card
import src.card.energy.EnergyCard
import src.card.energy.EnergyType
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

  def addCardToHand(c : Card) {
    _hand = _hand ++ List(c)
  }

  def dealPrizeCards() {
    for (i <- 0 until 6) {
      prizes(i) = Some(_deck(i))
    }
    _deck = _deck.slice(7, _deck.length)
  }
  
  private var _hand : Seq[Card] = List()

  def hand = _hand

  var _garbage : Seq[Card] = List()

  def garbage = _garbage

  private val _bench : Array[Option[PokemonCard]] = Array.fill(5) { None }

  def bench = _bench

  def setBench(pc : PokemonCard, i : Int) : Unit = {
    _bench(i) = Some(pc)
  }

  def benchIsFull = _bench.length == _bench.toList.flatten.length

  val _prizes : Array[Option[Card]] = Array.fill(6) { None }

  def prizes = _prizes

  private var _active : Option[PokemonCard] = None

  def setActive(pc : PokemonCard) {
    if (_active != None) {
      throw new Exception("Tried to overwrite current active")
    }
    _active = Some(pc)
  }

  def clearActive() {
    _active = None
  }

  def active = _active

  var notification : Option[String] = None

  var isTurn : Boolean = false

  var addedEnergy : Boolean = false

  def ownsMove(m : Move) : Boolean = existingActiveAndBenchCards.filter(_.ownsMove(m)).length > 0

  def ownerOfMove(m : Move) : Option[PokemonCard] = existingActiveAndBenchCards.find(_.ownsMove(m))

  def existingActiveAndBenchCards : Seq[PokemonCard] = return (List(active) ++ bench.toList).flatten

  def attachEnergyFromHand(pc : PokemonCard, hIndex : Int) : Unit = _hand(hIndex) match {
    case ec : EnergyCard => {
      pc.attachEnergy(ec)
      removeCardFromHand(hIndex)
      addedEnergy = true
    }
    case _ => throw new Exception("Tried to attach non-energy card from hand")
  }

  def discardEnergyFromCard(
      pc : PokemonCard,
      eType : EnergyType.Value = EnergyType.COLORLESS,
      cnt : Int = 1) {
    val eCards = pc.discardEnergy(eType, cnt)
    _garbage = _garbage ++ eCards
  }

  def discardSpecificEnergyFromCard(
    pc : PokemonCard,
    eIndices : Seq[Int]) {
    val tossedCards = pc.discardSpecificEnergy(eIndices)
    _garbage = _garbage ++ tossedCards 
  }

  def moveActiveToHand() : Unit = if (active.isDefined && !active.get.agility) {
    _hand = _hand ++ active.get.pickUp()
    _active = None
  }

  def pickUpCard(pc : PokemonCard) : Seq[Card] = {
    if (_active == Some(pc)) {
      clearActive()
      return pc.pickUp()
    }
    for (i <- 0 until 5) {
      if (_bench(i) == Some(pc)) {
        _bench(i) = None
        return pc.pickUp()
      }
    }
    throw new Exception("Could not find card to pick up")
  }

  def swapActiveAndBench(benchIndex : Int) {
    if (_active.isDefined) {
      if (_active.get.agility || _active.get.acid) {
        return
      }
    }
    val tmp = _active
    _active = _bench(benchIndex)
    _bench(benchIndex) = tmp
  }

  def swapBenchCards(bIndex1 : Int, bIndex2 : Int) : Unit = {
    val tmp = _bench(bIndex1)
    _bench(bIndex1) = _bench(bIndex2)
    _bench(bIndex2) = tmp
  }

  def evolveActiveCard(hIndex : Int) : Unit = hand(hIndex) match {
    case ep : EvolvedPokemon => if (ep.isEvolutionOf(active.get)) {
      ep.evolveOver(active.get)
      _active = Some(ep)
      removeCardFromHand(hIndex)
    }
    case _ => throw new Exception("Tried to evolve active with non-evolved pokemon")
  }

  def evolveBenchCard(hIndex : Int, bIndex : Int) : Unit = hand(hIndex) match {
    case ep : EvolvedPokemon => if (ep.isEvolutionOf(bench(bIndex).get)) {
      ep.evolveOver(bench(bIndex).get)
      _bench(bIndex) = Some(ep)
      removeCardFromHand(hIndex)
    }
    case _ => throw new Exception("Tried to evolve bench with non-evolved pokemon")
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

  def discardCards(cards : Seq[Card]) : Unit = _garbage = _garbage ++ cards

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

  private def setUiOrientationForList(uiSet : Set[CardUI.Value], list : Seq[Card]) {
    for (c : Card <- list) {
      c.setUiOrientation(uiSet)
    }
  }

  def setUiOrientationForPrize(uiSet : Set[CardUI.Value]) = setUiOrientationForList(uiSet, prizes.toList.flatten)

  def setUiOrientationForDeck(uiSet : Set[CardUI.Value]) = setUiOrientationForList(uiSet, deck)

  def setUIOrientationForBench(uiSet : Set[CardUI.Value]) = setUiOrientationForList(uiSet, bench.toList.flatten)

  def setUiOrientationForHand(uiSet : Set[CardUI.Value]) = setUiOrientationForList(uiSet, hand)

  def setUIOrientationForActiveAndBench(uiSet : Set[CardUI.Value]) = setUiOrientationForList(uiSet, existingActiveAndBenchCards)

  def cardWithActivatedPower : Option[PokemonCard] = {
    for (pc : PokemonCard <- existingActiveAndBenchCards) {
      pc.existingMoves.foreach {
        case ap : ActivePokemonPower => {
          if (ap.isActive) {
            return Some(pc)
          }
        }
        case _ => ()
      }
    }
    return None
  }

  


  def notify(msg : String) {
    notification = Some(msg)
  }

  def removeCardFromHand(handIndex : Int) {
    _hand = _hand.slice(0, handIndex) ++ _hand.slice(handIndex + 1, _hand.size)
  }

  def customJson(
    activeMoves : Option[JsArray] = None,
    benchMoves : Option[Seq[JsArray]] = None,
    handMoves : Option[Seq[JsArray]] = None,
    deckMoves : Option[Seq[JsArray]] = None,
    prizeMoves : Option[Seq[JsArray]] = None) : JsObject = {
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
       Identifier.DECK.toString -> (deckMoves match {
        case Some(m) => deckForCustomJson(m)
        case None => cardListToJsArray(deck)
       }),
       Identifier.HAND.toString -> (handMoves match {
        case Some(m) => handForCustomJson(m)
        case None => cardListToJsArray(hand)
        }),
       Identifier.GARBAGE.toString -> cardListToJsArray(garbage),
       Identifier.BENCH.toString -> (benchMoves match {
        case Some(m) => benchForCustomJson(m)
        case None => optionCardListToJsArray[PokemonCard](bench)
       }),
       Identifier.PRIZES.toString -> (prizeMoves match {
        case Some(m) => prizeForCustomJson(m)
        case None => optionCardListToJsArray(prizes)
       }),
       Identifier.NOTIFICATION.toString -> notifyJson,
       Identifier.IS_TURN.toString -> JsBoolean(isTurn))
  }

  def benchForCustomJson(benchMoves : Seq[JsArray]) : JsArray = {
    return bench.zipWithIndex.foldRight(new JsArray())((c, curr) => curr.prepend(customMoveJson(c._1, benchMoves(c._2))))
  }

  def prizeForCustomJson(prizeMoves : Seq[JsArray]) : JsArray = {
    return prizes.zipWithIndex.foldRight(new JsArray())((c, curr) => curr.prepend(customMoveJson(c._1, prizeMoves(c._2))))
  }

  def deckForCustomJson(deckMoves : Seq[JsArray]) : JsArray = {
    return deck.zipWithIndex.foldRight(new JsArray())((c, curr) => curr.prepend(customMoveJson(Some(c._1), deckMoves(c._2))))
  }

  def handForCustomJson(handMoves : Seq[JsArray]) : JsArray = {
    return hand.zipWithIndex.foldRight(new JsArray())((c, curr) => curr.prepend(customMoveJson(Some(c._1), handMoves(c._2))))
  }

  def customMoveJson(pc : Option[Card], benchMoves : JsArray) : JsObject = pc match {
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