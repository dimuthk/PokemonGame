package src.card.pokemon.base_set

import src.json.Identifier
import src.move._
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
	retreatCost = 1) {


}

private class Heal extends ActivePokemonPower("Heal", stateGenerator = Some(new HealStateGenerator())) {

  var usedHeal : Boolean = false

    class HealChooseSpecification(
        p : Player,
        benchAndActiveCards : Seq[PokemonCard]) extends ClickableCardRequest(
        "Select Pokemon",
        "Choose a pokemon to heal.",
        p,
        1,
        benchAndActiveCards)

  override def update = (owner, opp, pc, turnSwapped, isActive) => {
    super.update(owner, opp, pc, turnSwapped, isActive)
    Logger.debug("updated venomoth?")
    if (turnSwapped && owner.isTurn) {
      usedHeal = false
    }
    if (usedHeal) {
      status = Status.DISABLED
    }
  }

  override def perform = (owner, opp, args) => togglePower()

	/*override def perform = (owner, opp, args) => args.length match {
    case 0 => Some(new HealChooseSpecification(owner, owner.existingActiveAndBenchCards))
    case _ =>  usedHeal match {
      case true => throw new Exception("Attempted to reuse heal after it had been used this turn!")
      case false => {
        usedHeal = true
        var rawIndex = args.head.toInt
        (owner.active.isDefined, args.head.toInt) match {
          case (true, 0) => {
            owner.active.get.heal(10)
          }
          case (false, _) => {
            rawIndex = rawIndex - 1
            val benchIndex = getRealIndexFor(rawIndex, owner.bench)
            owner.bench(benchIndex).get.heal(10)
          }
          case _ => {
            val benchIndex = getRealIndexFor(rawIndex, owner.bench)
            owner.bench(benchIndex).get.heal(10)
          }
        }
        None
      }
    }
  }*/

}

class HealMoveInterpreter extends CustomMoveInterpreter {

  def attackFromActive = (owner, opp, move, args) => {
    val pc = owner.cardWithActivatedPower
    pc.get.firstMove.get.perform(owner, opp, args)
  }

  def attackFromBench = (owner, opp, move, args) => {
    val pc = owner.cardWithActivatedPower
    pc.get.firstMove.get.perform(owner, opp, args)
  }

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

    val activeJson = moveListToJsArray(List(header, healMoveJson))
    val benchJson = moveListToJsArray(List(healMoveJson))

    val ownerJson = owner.customJson(activeMoves = Some(activeJson), benchMoves = Some(benchJson))
    Logger.debug("VILEPLUME " + ownerJson \ "ACTIVE" \ "MOVES" + "")
    (ownerJson, opp.toJson)
  }

  val header : JsObject = Json.obj(
    Identifier.MOVE_NAME.toString -> "Heal",
    Identifier.MOVE_STATUS.toString -> Status.ACTIVATED)

  val healMoveJson : JsObject = Json.obj(
    Identifier.MOVE_NAME.toString -> "Heal",
    Identifier.MOVE_STATUS.toString -> Status.ENABLED)

  def moveListToJsArray(list : Seq[JsObject]) : JsArray = {
    return list.foldRight(new JsArray())((m, curr) => curr.prepend(m))
  }

  def optionMoveToJson(om : Option[Move]) : JsObject = om match {
    case Some(m) => m.toJson
    case None => Placeholder.toJson
  }

}