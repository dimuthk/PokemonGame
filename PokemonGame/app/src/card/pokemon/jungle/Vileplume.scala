package src.card.pokemon.base_set

import src.json.Identifier
import src.move._
import src.move.Status._
import src.board.intermediary.IntermediaryRequest
import src.board.intermediary.IntermediaryRequest._
import src.board.move._
import src.board.state._
import src.board.intermediary.ClickableCardRequest
import src.card.CardUI
import src.card.CardUI._
import src.move.MoveBuilder._
import src.card.condition.StatusCondition
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck
import play.api.Logger
import src.card.Placeholder
import play.api.libs.json._

class Vileplume extends StageTwoPokemon(
	"Vileplume",
	"Vileplume-Jungle-15.jpg",
	Deck.JUNGLE,
	Identifier.VILEPLUME,
	id = 45,
	maxHp = 80,
	firstMove = Some(new Heal()),
	secondMove = Some(new Move(
		"Petal Dance",
    3,
    Map(EnergyType.GRASS -> 3)) {
      def perform = (owner, opp, args) => {
        multipleHitAttack(owner, opp, 40, 3)
        owner.active.get.inflictStatus(StatusCondition.CONFUSED)
	    }
  }),
	energyType = EnergyType.GRASS,
	weakness = Some(EnergyType.FIRE),
	retreatCost = 2)

private class Heal extends ActivePokemonPower(
  "Heal",
  stateGenerator = Some(new HealStateGenerator()),
  moveInterpreter = Some(new HealMoveInterpreter())) {

  var usedHeal : Boolean = false

  override def update = (owner, opp, pc, turnSwapped, isActive) => {
    super.update(owner, opp, pc, turnSwapped, isActive)
    if (turnSwapped && owner.isTurn) {
      usedHeal = false
    }
    if (usedHeal) {
      status = Status.DISABLED
    }
    Logger.debug("heal status? " + status)
  }

  override def perform = (owner, opp, args) => {
    usedHeal = true
    togglePower()
  }

}

class HealMoveInterpreter extends CustomMoveInterpreter {

  def getHealMove(owner : Player) : ActivePokemonPower = owner.cardWithActivatedPower match {
    case Some(v : Vileplume) => v.firstMove match {
      case Some(ap : ActivePokemonPower) => ap
      case _ => throw new Exception("Vileplume did not have heal as its move for some reason")
    }
    case _ => throw new Exception("Did not find Vileplume as the activated card")
  }

  private def handleHeal(pc : PokemonCard, p : Player, moveNum : Int) {
    val ap = getHealMove(p)   
    moveNum match {
      case 1 => if (pc != p.cardWithActivatedPower.get) {
        pc.heal(10)
      }
      case 2 => pc.heal(10)
      case _ => throw new Exception("Unexpected move num for Vileplume heal")
    }
    ap.togglePower()
  }

  override def handleCommand = (pData, moveCmd, args) => moveCmd match {
    case AttackFromActive(moveNum) => handleHeal(pData.owner.active.get, pData.owner, moveNum)
    case AttackFromBench(bIndex, moveNum) => handleHeal(pData.owner.bench(bIndex).get, pData.owner, moveNum)
    case _ => throw new Exception("Unsupported move command for heal")
  }

}

class HealStateGenerator extends CustomStateGenerator {

  def generateUiFor = (p, cmd, isSouth) => (cmd, isSouth) match {
    // Active and bench cards are visible to allow heal selection.
    case (_ : ActiveOrBench, true) => Set(FACE_UP, CLICKABLE, DISPLAYABLE, USABLE)
    // Hand is deactivated.
    case (Hand(_), true) => Set()
    case _ => DefaultStateGenerator.generateUiFor(p, cmd, isSouth)
  }

  def uiForActivatedCard = (p) => Set(FACE_UP, CLICKABLE, DISPLAYABLE, DRAGGABLE, USABLE)

  override def setCustomMoveFor = (p, cmd, isSouth) => (cmd, isSouth) match {
    case (Active(pc), true) => Some(moveListToJsArray(List(otherCard(pc))))
    case (Bench(pc), true) => Some(moveListToJsArray(List(otherCard(pc))))
    case _ => None
  }

  override def customMoveForActivatedCard = (p) => p.cardWithActivatedPower match {
    case Some(pc) => Some(moveListToJsArray(List(mainCard(pc), otherCard(pc))))
    case None => None
  }

  def otherCard(pc : PokemonCard) : JsObject = Json.obj(
    Identifier.MOVE_NAME.toString -> "Heal",
    Identifier.MOVE_STATUS.toString -> (if (pc.currHp == pc.maxHp) DISABLED else ENABLED))

  def mainCard(pc : PokemonCard) : JsObject = Json.obj(
    Identifier.MOVE_NAME.toString -> "Heal",
    Identifier.MOVE_STATUS.toString -> ACTIVATED)

}