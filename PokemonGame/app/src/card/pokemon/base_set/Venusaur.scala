package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.move.ActivePokemonPower
import src.board.intermediary.IntermediaryRequest
import src.board.drag._
import src.board.state._
import src.board.state.CustomStateGenerator
import src.board.state.DefaultStateGenerator
import src.player.Player
import src.card.energy.EnergyCard
import src.card.energy.EnergyType
import src.card.CardUI
import src.card.CardUI._
import src.card.pokemon._
import src.card.Deck
import play.api.libs.json._
import play.api.Logger

class Venusaur extends BasicPokemon(
    "Venusaur",
    "Venusaur-Base-Set-15.jpg",
    Deck.BASE_SET,
    Identifier.VENUSAUR,
    id = 3,
    maxHp = 100,
    firstMove = Some(new ActivePokemonPower(
      "Energy Trans",
      dragInterpreter = Some(new EnergyTransDrag()),
      stateGenerator = Some(new EnergyTransState())) {
        def perform = (owner, opp, args) => togglePower()
    }
    ),
    secondMove = Some(new Move(
      "Solarbeam",
      4,
      Map(EnergyType.GRASS -> 4)) {
      def perform = (owner, opp, args) => standardAttack(owner, opp, 60)
    }),
    energyType = EnergyType.GRASS,
    weakness = Some(EnergyType.FIRE),
    retreatCost = 2)


private class EnergyTransState extends CustomStateGenerator{

  def uiForActivatedCard = (p) => Set(FACE_UP, CLICKABLE, DISPLAYABLE, DRAGGABLE, USABLE)

  def generateUiFor = (p, cmd, isSouth) => (cmd, isSouth) match {
    case (_ : ActiveOrBench, true) => Set(FACE_UP, DRAGGABLE)
    case _ => DefaultStateGenerator.generateUiFor(p, cmd, isSouth)
  }

}

private class EnergyTransDrag extends CustomDragInterpreter {

  override def handleCommand = (pData, dragCmd, args) => dragCmd match {
    case BenchToBench(bIndex1, bIndex2) => if (pData.owner.bench(bIndex2).isDefined) {
      transferLeafEnergy(pData.owner.bench(bIndex1).get, pData.owner.bench(bIndex2).get)
    }
    case BenchToActive(bIndex) => if (pData.owner.active.isDefined) {
      transferLeafEnergy(pData.owner.bench(bIndex).get, pData.owner.active.get)
    }
    case ActiveToBench(bIndex) => if (pData.owner.bench(bIndex).isDefined) {
      transferLeafEnergy(pData.owner.active.get, pData.owner.bench(bIndex).get)
    }
    case _ => ()
  }

  private def transferLeafEnergy(drag : PokemonCard, drop : PokemonCard) : Unit 
    = drop.attachEnergy(drag.discardEnergy(eType = EnergyType.GRASS))

}

