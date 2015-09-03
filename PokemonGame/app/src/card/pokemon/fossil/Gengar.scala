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

class Gengar extends StageTwoPokemon(
	"Gengar",
	"Gengar-Fossil-5.jpg",
	Deck.FOSSIL,
	Identifier.GENGAR,
	id = 94,
	maxHp = 80,
	firstMove = Some(new ActivePokemonPower(
		"Curse",
		dragInterpreter = Some(new CurseDrag()),
      	stateGenerator = Some(new CurseState())) {
			def perform = (owner, opp, args) => togglePower()
      	}),
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
    owner.setUIOrientationForActiveAndBench(Set())
    owner.setUiOrientationForHand(Set())

    // Opponent's active and bench are draggable
    opp.setUIOrientationForActiveAndBench(Set(FACE_UP, DRAGGABLE))
    opp.setUiOrientationForHand(Set())

    // Gengar must still be usable to deactivate power.
    //owner.setUIOrientationForActiveAndBench(Set(FACE_UP, CLICKABLE, DISPLAYABLE, USABLE))
    (owner.toJson, opp.toJson)
  }

}

private class CurseDrag extends CustomDragInterpreter {

  override def benchToBench(p : Player, benchIndex1 : Int, benchIndex2 : Int) : Unit = {
    if (p.bench(benchIndex2).isDefined) {
      swapDamage(p.bench(benchIndex1).get, p.bench(benchIndex2).get)
    }
  }

  override def benchToActive(p : Player, benchIndex : Int) : Unit = {
    if (p.active.isDefined) {
      swapDamage(p.bench(benchIndex).get, p.active.get)
    }
  }

  override def activeToBench(p : Player, benchIndex : Int) : Unit = {
    if (p.bench(benchIndex).isDefined) {
      swapDamage(p.active.get, p.bench(benchIndex).get)
    }
  }

  private def swapDamage(drag : PokemonCard, drop : PokemonCard) : Unit = {
    if (drop.currHp > 10 && drag.currHp < drag.maxHp) {
      drag.heal(10)
      drop.takeDamage(None, 10)
    }
  }

}
