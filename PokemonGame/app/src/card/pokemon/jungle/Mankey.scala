package src.card.pokemon.base_set

import src.json.Identifier
import src.board.intermediary.SingleDisplay
import src.board.move._
import src.board.state._
import src.move._
import src.move.Status._
import src.card.Card
import src.card.CardUI
import src.card.CardUI._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck._
import src.card.Placeholder
import play.api.libs.json._
import play.api.Logger

class Mankey extends BasicPokemon(
	"Mankey",
	"Mankey-Jungle-55.jpg",
	JUNGLE,
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

class PeekStateGenerator extends CustomStateGenerator {

  def uiForActivatedCard = (p) => Set(FACE_UP, CLICKABLE, DISPLAYABLE, DRAGGABLE, USABLE)

  def generateUiFor = (p, cmd, isSouth) => cmd match {
    case _ : ActiveOrBench => Set(FACE_UP)
    case Hand() => isSouth match {
      case true => Set(FACE_UP, CLICKABLE)
      case false => Set(CLICKABLE, USABLE)
    }
    case _ => Set(CLICKABLE, USABLE)
  }

  override def setCustomMoveFor = (p, cmd, isSouth) => cmd match {
    case Hand() => isSouth match {
      case true => None
      case false => Some(jsonForCard)
    }
    case Prize() => Some(jsonForCard)
    case Deck() => Some(jsonForCard)
    case _ => None
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

  override def handleCommand = (pData, moveCmd, _) => ()

}