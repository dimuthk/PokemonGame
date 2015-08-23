package src.card.pokemon

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType

class Bulbasaur extends PokemonCard(
    "Bulbasaur",
    "Bulbasaur-Base-Set-44.jpg",
    Identifier.BULBASAUR,
    id = 1,
    maxHp = 40,
    firstMove = Some(new LeechSeed()),
    energyType = EnergyType.GRASS,
    weakness = Some(EnergyType.FIRE),
    resistance = None,
    retreatCost = 1)

private class LeechSeed extends Move(
    "Leech Seed",
    2,
    Map(EnergyType.GRASS -> 2)) {

  override def perform(owner : Player, opp : Player) = {
    if (calculateDmg(owner, opp, 20) > 0) {
      owner.active.get.heal(10)
    }
    standardAttack(owner, opp, 20)
  }
}
