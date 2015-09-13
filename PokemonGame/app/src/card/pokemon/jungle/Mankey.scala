package src.card.pokemon.base_set

import src.json.Identifier
import src.board.intermediary.SingleDisplay
import src.board.move._
import src.board.state.CustomStateGenerator
import src.move._
import src.move.Status._
import src.card.Card
import src.card.CardUI
import src.card.CardUI._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck
import src.card.Placeholder
import play.api.libs.json._
import play.api.Logger

class Mankey extends BasicPokemon(
	"Mankey",
	"Mankey-Jungle-55.jpg",
	Deck.JUNGLE,
	Identifier.MANKEY,
	id = 56,
	maxHp = 30,
	firstMove = Some(new Peek()),
	secondMove = Some(new Move(
		"Scratch",
        1) {
			def perform = (owner, opp, args) => standardAttack(owner, opp, 10)
        }),
	energyType = EnergyType.FIGHTING,
	weakness = Some(EnergyType.PSYCHIC),
	retreatCost = 0)

private class Peek extends ActivePokemonPower(
  "Peek",
  moveInterpreter = Some(new PeekMoveInterpreter()),
  stateGenerator = Some(new PeekStateGenerator())) {

  var usedPeak : Boolean = false

  override def update = (owner, opp, pc, turnSwapped, isActive) => {
    super.update(owner, opp, pc, turnSwapped, isActive)
    if (turnSwapped && owner.isTurn) {
      usedPeak = false
      Logger.debug("turnSwapped????")
    }
    if (usedPeak) {
      status = Status.DISABLED
    }
    Logger.debug("MANKEY: " + turnSwapped + ", " + usedPeak)
  }

  override def perform = (owner, opp, args) => {
  	Logger.debug("MANKEY perform")
    usedPeak = true
    togglePower()
  }

}

class PeekStateGenerator extends CustomStateGenerator(true, false) {

  override def generateForOwner = (owner, opp, interceptor) => {
    // Active, bench and handcards are visible but not interactable.
    owner.setUIOrientationForActiveAndBench(Set(FACE_UP))
    owner.setUiOrientationForHand(Set(FACE_UP))

    // Opponent active and bench cards are visible but not clickable.
    opp.setUIOrientationForActiveAndBench(Set(FACE_UP))

    // Opponent's hand must be interactable to allow for peek
    opp.setUiOrientationForHand(Set(CLICKABLE, USABLE))

    // Decks are peekable as well
    owner.setUiOrientationForDeck(Set(CLICKABLE, USABLE))
    opp.setUiOrientationForDeck(Set(CLICKABLE, USABLE))

    // Prizes too!
    owner.setUiOrientationForPrize(Set(CLICKABLE, USABLE))
    opp.setUiOrientationForPrize(Set(CLICKABLE, USABLE))

    interceptor.firstMove.get.status = Status.ACTIVATED

    val oppHandJson = for (_ <- opp.hand) yield jsonForCard

    val ownerDeckJson = for (_ <- owner.deck) yield jsonForCard
    val oppDeckJson = for (_ <- opp.deck) yield jsonForCard

    val ownerPrizeJson = for (op <- owner.prizes) yield optionJsonForCard(op)
    val oppPrizeJson = for (op <- opp.prizes) yield optionJsonForCard(op)

    val ownerJson = owner.customJson(
    	deckMoves = Some(ownerDeckJson),
    	prizeMoves = Some(ownerPrizeJson))

    val oppJson = opp.customJson(
    	deckMoves = Some(oppDeckJson),
    	prizeMoves = Some(oppPrizeJson),
    	handMoves = Some(oppHandJson))

    (ownerJson, oppJson)
  }

  def optionJsonForCard(oc : Option[Card]): JsArray = oc match {
  	case Some(c) => jsonForCard
  	case None => moveListToJsArray(List(Placeholder.toJson))
  }

  def jsonForCard : JsArray = moveListToJsArray(List(Json.obj(
  	Identifier.MOVE_NAME.toString -> "Peek",
  	Identifier.MOVE_STATUS.toString -> ENABLED)))

  def moveListToJsArray(list : Seq[JsObject]) : JsArray = {
    return list.foldRight(new JsArray())((m, curr) => curr.prepend(m))
  }

}

class PeekMoveInterpreter extends CustomMoveInterpreter {

	def getMankey(owner : Player) : Mankey = owner.cardWithActivatedPower match {
		case Some(m : Mankey) => m
		case _ => throw new Exception(" couldn't find mankey")
	}

  def getPeekMove(owner : Player) : ActivePokemonPower = owner.cardWithActivatedPower match {
    case Some(m : Mankey) => m.firstMove match {
      case Some(ap : ActivePokemonPower) => ap
      case _ => throw new Exception("Mankey did not have peek as its move for some reason")
    }
    case _ => throw new Exception("Did not find Mankey as the activated card")
  }

  override def requestAdditional = (pData, moveCmd, _) => moveCmd match {
    case AttackFromHand(hIndex, _) => {
      getPeekMove(pData.owner).togglePower()
      Some(new SingleDisplay("Result Card", "Here is the card", pData.owner, pData.opp.hand(hIndex)))
    }
    case AttackFromPrize(pIndex, _) => {
      val p = if (pData.isOwner) pData.owner else pData.opp
      getPeekMove(pData.owner).togglePower()
      Some(new SingleDisplay("Result Card", "Here is the card", pData.owner, p.prizes(pIndex).get))
    }
    case AttackFromDeck(_) => {
      val p = if (pData.isOwner) pData.owner else pData.opp
      getPeekMove(pData.owner).togglePower()
      Some(new SingleDisplay("Result Card", "Here is the card", pData.owner, p.deck(0)))
    }
    case _ => throw new Exception("Unrecognized move action for peek")
  }

  override def handleMove = (pData, moveCmd, _) => ()

}