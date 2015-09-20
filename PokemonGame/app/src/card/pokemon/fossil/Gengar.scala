package src.card.pokemon.base_set

import src.card.condition.StatusCondition
import src.json.Identifier
import src.board.drag._
import src.board.state._
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

private class CurseState extends CustomStateGenerator {

  // Gengar must still be usable to deactivate power.
  def uiForActivatedCard = (p) => Set(FACE_UP, CLICKABLE, DISPLAYABLE, USABLE)

  def generateUiFor = (p, cmd, isSouth) => cmd match {
    // Opponent's active and bench are draggable. user's stuff
    // is deactivated.
    case _ : ActiveOrBench => isSouth match {
      case true => Set(FACE_UP)
      case false => Set(FACE_UP, DRAGGABLE) 
    }
    case _ => DefaultStateGenerator.generateUiFor(p, cmd, isSouth)
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

  override def handleCommand = (pData, dragCmd, _) => dragCmd match {
    case BenchToBench(bIndex1, bIndex2) => if (pData.opp.bench(bIndex2).isDefined) {
      swapDamage(pData.opp.bench(bIndex1).get, pData.opp.bench(bIndex2).get)
      findCurse(pData.owner).togglePower()
    }
    case BenchToActive(bIndex) => if (pData.opp.active.isDefined) {
      swapDamage(pData.opp.bench(bIndex).get, pData.opp.active.get)
      findCurse(pData.owner).togglePower()
    }
    case ActiveToBench(bIndex) => if (pData.opp.bench(bIndex).isDefined) {
      swapDamage(pData.opp.active.get, pData.opp.bench(bIndex).get)
      findCurse(pData.owner).togglePower()
    }
    case _ => ()
  }

  private def swapDamage(drag : PokemonCard, drop : PokemonCard) : Unit = {
    if (drop.currHp > 10 && drag.currHp < drag.maxHp) {
      drag.heal(10)
      drop.takeDamage(None, 10)
    }
  }

}
