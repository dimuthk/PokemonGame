package src.card.pokemon.base_set

import src.card.Deck
import src.card.pokemon._
import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.move.ActivePokemonPower
import src.board.drag._
import src.player.Player
import src.card.energy.EnergyCard
import src.card.energy.EnergyType
import src.card.energy.WaterEnergy

class Blastoise extends StageTwoPokemon(
    "Blastoise",
    "Blastoise-Base-Set-2.jpg",
    Deck.BASE_SET,
    Identifier.BLASTOISE,
    id = 9,
    maxHp = 100,
    firstMove = Some(new ActivePokemonPower(
      "Rain Dance",
      dragInterpreter = Some(new RainDanceDrag())) {
        def perform = (owner, opp, args) => togglePower()
      }),
    secondMove = Some(new Move(
      "Hydro Pump",
      3,
      Map(EnergyType.WATER -> 3)) {
        def perform = (owner, opp, args) => standardAttackPlusExtra(owner, opp, 40, EnergyType.WATER, 3)
      }),
    energyType = EnergyType.WATER,
    weakness = Some(EnergyType.THUNDER),
    retreatCost = 3)


private class RainDanceDrag extends CustomDragInterpreter {

  override def handleDrag = (pData, dragCmd, args) => dragCmd match {
    case HandToActive(hIndex) => pData.owner.active match {
      case Some(active) => attachWaterEnergy(pData.owner, active, hIndex)
      case None => ()
    }
    case HandToBench(hIndex, bIndex) => pData.owner.bench(bIndex) match {
      case Some(bc) => attachWaterEnergy(pData.owner, bc, hIndex)
      case None => ()
    }
    case _ => ()
  }

  private def attachWaterEnergy(p : Player, pc : PokemonCard, handIndex : Int) : Unit = p.hand(handIndex) match {
    case ec : EnergyCard => if (ec.eType == EnergyType.WATER) {
      p.attachEnergyFromHand(pc, handIndex)
    }
    case _ => ()
  }

}
