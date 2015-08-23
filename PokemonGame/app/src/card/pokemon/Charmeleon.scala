package src.card.pokemon

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType

class Charmeleon extends PokemonCard(
    "Charmeleon",
    "Charmeleon-Base-Set-24.jpg",
    Identifier.CHARMELEON,
    id = 2,
    maxHp = 80,
    firstMove = Some(new Slash()),
    secondMove = Some(new Flamethrower()),
    energyType = EnergyType.FIRE,
    weakness = Some(EnergyType.WATER),
    retreatCost = 1,
    evolutionStage = EvolutionStage.STAGE_ONE) {

  override def isEvolutionOf(pokemon : PokemonCard) = pokemon.id == 4

}

private class Slash extends Move(
  "Slash",
  3,
  Map()) {

  override def perform(owner : Player, opp : Player) = standardAttack(owner, opp, 30)
}

private class Flamethrower extends Move(
  "Flamethrower",
  3,
  Map(EnergyType.FIRE -> 2)) {

  override def perform(owner : Player, opp : Player) =
      energyDiscardAttack(owner, opp, 50, EnergyType.FIRE)
}

