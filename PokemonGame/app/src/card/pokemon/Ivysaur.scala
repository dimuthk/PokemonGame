package src.card.pokemon

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType

class Ivysaur extends PokemonCard(
    "Ivysaur",
    "Ivysaur-Base-Set-30.jpg",
    Identifier.IVYSAUR,
    id = 2,
    maxHp = 60,
    firstMove = Some(new VineWhip()),
    secondMove = Some(new Poisonpowder()),
    energyType = EnergyType.GRASS,
    weakness = Some(EnergyType.FIRE),
    retreatCost = 1,
    evolutionStage = EvolutionStage.STAGE_ONE) {

  override def isEvolutionOf(pokemon : PokemonCard) = pokemon.id == 1

}

private class VineWhip extends Move(
  "Vine Whip",
  3,
  Map(EnergyType.GRASS -> 1)) {

  override def perform(owner : Player, opp : Player) = standardAttack(owner, opp, 30)

}

private class Poisonpowder extends Move(
  "Poisonpowder",
  3,
  Map(EnergyType.GRASS -> 3)) {

  override def perform(owner : Player, opp : Player) = poisonAttack(owner, opp, 20)

}

