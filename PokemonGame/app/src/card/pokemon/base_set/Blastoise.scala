package src.card.pokemon.base_set

import src.card.Deck
import src.card.pokemon._
import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.move.ActivePokemonPower
import src.board.drag.CustomDragInterpreter
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
    firstMove = Some(new RainDance()),
    secondMove = Some(new HydroPump()),
    energyType = EnergyType.WATER,
    weakness = Some(EnergyType.THUNDER),
    retreatCost = 3)

private class RainDance extends ActivePokemonPower(
  "Rain Dance",
  dragInterpreter = Some(new RainDanceDrag()))


private class HydroPump extends Move(
  "HydroPump",
  3,
  Map(EnergyType.WATER -> 3)) {

  override def perform(owner : Player, opp : Player)
      = standardAttackPlusExtra(owner, opp, 40, EnergyType.WATER, 3)
}


private class RainDanceDrag extends CustomDragInterpreter {

  override def benchToBench(p : Player, benchIndex1 : Int, benchIndex2 : Int) : Unit = ()

  override def benchToActive(p : Player, benchIndex : Int) : Unit = ()

  override def activeToBench(p : Player, benchIndex : Int) : Unit = ()

  override def handToActive(p : Player, handIndex : Int) : Unit = {
    if (p.active.isDefined) {
      attachWaterEnergy(p, p.active.get, handIndex)
    }
  }

  override def handToBench(p : Player, handIndex : Int, benchIndex : Int) : Unit = {
    if (p.bench(benchIndex).isDefined) {
      attachWaterEnergy(p, p.bench(benchIndex).get, handIndex)
    }
  }

  private def attachWaterEnergy(p : Player, pc : PokemonCard, handIndex : Int) {
    p.hand(handIndex) match {
      case ec : EnergyCard => {
        if (ec.eType == EnergyType.WATER) {
          pc.energyCards = pc.energyCards ++ List(ec)
          p.removeCardFromHand(handIndex)
        }
      }
      case _ => ()
    }
  }

}
