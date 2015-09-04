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

  def handToActive = (p, _, _, handIndex, _) => {
    if (p.active.isDefined) {
      attachWaterEnergy(p, p.active.get, handIndex)
    }
    None
  }

  def  handToBench = (p, _, _, handIndex, benchIndex, _) => {
    if (p.bench(benchIndex).isDefined) {
      attachWaterEnergy(p, p.bench(benchIndex).get, handIndex)
    }
    None
  }

  def activeToBench = (_, _, _, _, _) => None

  def benchToActive = (_, _, _, _, _) => None

  def benchToBench = (_, _, _, _, _, _) => None 

  private def attachWaterEnergy(p : Player, pc : PokemonCard, handIndex : Int) : Unit = p.hand(handIndex) match {
    case ec : EnergyCard => {
      if (ec.eType == EnergyType.WATER) {
        pc.energyCards = pc.energyCards ++ List(ec)
        p.removeCardFromHand(handIndex)
      }
    }
    case _ => ()
  }

}
