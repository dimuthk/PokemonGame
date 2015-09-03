package src.card.pokemon.base_set

import src.card.condition.StatusCondition
import src.json.Identifier
import src.board.drag.CustomDragInterpreter
import src.board.state.CustomStateGenerator
import src.move._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.CardUI
import src.card.CardUI._
import src.card.pokemon._
import src.card.Deck
import play.api.Logger

class Gengar extends StageTwoPokemon(
	"Gengar",
	"Gengar-Fossil-5.jpg",
	Deck.FOSSIL,
	Identifier.GENGAR,
	id = 94,
	maxHp = 80,
	firstMove = Some(new Curse()),
	secondMove = Some(new BenchSelectAttack(
      "Dark Mind",
      mainDmg = 30,
      benchDmg = 10,
      numBenchSelects = 1,
      3,
      Map(EnergyType.PSYCHIC -> 3))),
	energyType = EnergyType.PSYCHIC,
	resistance = Some(EnergyType.FIGHTING),
	retreatCost = 1)

private class CurseState extends CustomStateGenerator(true, false) {

  override def generateForOwner = (owner, opp, interceptor) => {
  	// All of owner's stuff is deactivated.
    owner.setUIOrientationForActiveAndBench(Set(FACE_UP))
    owner.setUiOrientationForHand(Set())

    // Opponent's active and bench are draggable
    opp.setUIOrientationForActiveAndBench(Set(FACE_UP, DRAGGABLE))
    opp.setUiOrientationForHand(Set())

    // Gengar must still be usable to deactivate power.
    interceptor.setUiOrientation(Set(FACE_UP, CLICKABLE, DISPLAYABLE, USABLE))
    interceptor.firstMove.get.status = Status.ACTIVATED
    interceptor.secondMove.get.status = Status.DISABLED
    (owner.toJson, opp.toJson)
  }

}

private class Curse extends ActivePokemonPower(
  "Curse",
  dragInterpreter = Some(new CurseDrag()),
  stateGenerator = Some(new CurseState())) {

  var usedCurse : Boolean = false

  override def update = (owner, opp, pc, turnSwapped, isActive) => {
    super.update(owner, opp, pc, turnSwapped, isActive)
    if (turnSwapped && owner.isTurn) {
      usedCurse = false
    }
    if (usedCurse) {
      status = Status.DISABLED
    }
    Logger.debug("update curse status? " + status)
  }

  override def perform = (owner, opp, args) => {
    usedCurse = true
    togglePower()
  }

} 

private class CurseDrag extends CustomDragInterpreter {

  def findCurse(owner : Player) : Curse =
    owner.cardWithActivatedPower.get.firstMove match {
      case Some(c : Curse) => c
      case _ => throw new Exception("Couldn't find curse")
    }

	def benchToBench = (owner, opp, _, benchIndex1, benchIndex2) => {
		if (opp.bench(benchIndex2).isDefined) {
      swapDamage(opp.bench(benchIndex1).get, opp.bench(benchIndex2).get)
      findCurse(owner).togglePower()
    }
    None
	}

	def benchToActive = (owner, opp, _, benchIndex) => {
		if (opp.active.isDefined) {
      		swapDamage(opp.bench(benchIndex).get, opp.active.get)
          findCurse(owner).togglePower()
    	}
    	None
	}

	def activeToBench = (owner, opp, _, benchIndex) => {
		if (opp.bench(benchIndex).isDefined) {
      		swapDamage(opp.active.get, opp.bench(benchIndex).get)
          findCurse(owner).togglePower()
    	}
    	None
	}

	def handToActive = (_, _, _, _) => None

	def handToBench = (_, _, _, _, _) => None

  private def swapDamage(drag : PokemonCard, drop : PokemonCard) : Unit = {
    if (drop.currHp > 10 && drag.currHp < drag.maxHp) {
      drag.heal(10)
      drop.takeDamage(None, 10)
    }
  }

}
