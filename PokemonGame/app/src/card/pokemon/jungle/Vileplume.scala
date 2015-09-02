package src.card.pokemon.base_set

import src.json.Identifier
import src.move._
import src.move.Status._
import src.board.intermediary.IntermediaryRequest
import src.board.intermediary.IntermediaryRequest._
import src.board.move.CustomMoveInterpreter
import src.board.state.CustomStateGenerator
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

class Vileplume extends BasicPokemon(
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
				owner.active.get.statusCondition = Some(StatusCondition.CONFUSED)
        None
			}
        }),
	energyType = EnergyType.GRASS,
	weakness = Some(EnergyType.FIRE),
	retreatCost = 2) {


}

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

  def attackFromActive = (owner, opp, _, moveNum, args) => {
    val active = owner.active.get
    val ap = getHealMove(owner)
    moveNum match {
      case 1 => owner.cardWithActivatedPower.get == active match {
        case true => ap.togglePower()
        case false => {
          active.heal(10)
          ap.togglePower()
        }
      }
      case 2 => {
        active.heal(10)
        ap.togglePower()
      }
      case _ => throw new Exception("Unexpected move num for Vileplume heal")
    }
  }

  def attackFromBench = (owner, opp, _, benchIndex, moveNum, args) => {
    val bc = owner.bench(benchIndex).get
    val ap = getHealMove(owner)
    moveNum match {
      case 1 => owner.cardWithActivatedPower.get == bc match {
        case true => ap.togglePower()
        case false => {
          bc.heal(10)
          ap.togglePower()
        }
      }
      case 2 => {
        bc.heal(10)
        ap.togglePower()
      }
      case _ => throw new Exception("Unexpected move num for Vileplume heal")
    }
  }

  def attackFromHand = (_, _, _, _, _, _) => throw new Exception("unsupported")

  def attackFromPrize = (_, _, _, _, _, _) => throw new Exception("unsupported")

  def attackFromDeck = (_, _, _, _, _) => throw new Exception("unsupported") 

}

class HealStateGenerator extends CustomStateGenerator(true, false) {

  override def generateForOwner = (owner, opp, interceptor) => {
    // Active and bench cards are visible to allow heal selection.
    owner.setUIOrientationForActiveAndBench(Set(FACE_UP, CLICKABLE, DISPLAYABLE, USABLE))
    // Hand is deactivated.
    owner.setUiOrientationForHand(Set())

    // Opponent active and bench cards are visible but not clickable.
    opp.setUIOrientationForActiveAndBench(Set(FACE_UP))
    opp.setUiOrientationForHand(Set())

    val activeJson = jsonForCard(owner, owner.active, interceptor)
    val benchJson = for (obc <- owner.bench) yield jsonForCard(owner, obc, interceptor)

    val ownerJson = owner.customJson(activeMoves = Some(activeJson), benchMoves = Some(benchJson))
    Logger.debug("VILEPLUME " + ownerJson \ "ACTIVE" \ "MOVES" + "")
    (ownerJson, opp.toJson)
  }

  def jsonForCard(owner : Player, opc : Option[PokemonCard], interceptor : PokemonCard) : JsArray = opc match {
    case Some(pc) => pc == interceptor match {
      case true => moveListToJsArray(List(mainCard(pc), otherCard(pc)))
      case false => moveListToJsArray(List(otherCard(pc)))
    }
    case None => moveListToJsArray(List(Placeholder.toJson))
  }

  def otherCard(pc : PokemonCard) : JsObject = Json.obj(
    Identifier.MOVE_NAME.toString -> "Heal",
    Identifier.MOVE_STATUS.toString -> (if (pc.currHp == pc.maxHp) DISABLED else ENABLED))

  def mainCard(pc : PokemonCard) : JsObject = Json.obj(
    Identifier.MOVE_NAME.toString -> "Heal",
    Identifier.MOVE_STATUS.toString -> ACTIVATED)

  def moveListToJsArray(list : Seq[JsObject]) : JsArray = {
    return list.foldRight(new JsArray())((m, curr) => curr.prepend(m))
  }

  def optionMoveToJson(om : Option[Move]) : JsObject = om match {
    case Some(m) => m.toJson
    case None => Placeholder.toJson
  }

}