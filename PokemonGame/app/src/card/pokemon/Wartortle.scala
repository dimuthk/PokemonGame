package src.card.pokemon

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.condition.GeneralCondition
import src.card.condition.WithdrawCondition
import src.card.energy.EnergyType

class Wartortle extends PokemonCard(
    "Wartortle",
    "Wartortle-Base-Set-42.jpg",
    Identifier.IVYSAUR,
    id = 8,
    maxHp = 70,
    firstMove = Some(new WithdrawWortortle()),
    secondMove = Some(new WartortleBite()),
    energyType = EnergyType.WATER,
    weakness = Some(EnergyType.THUNDER),
    retreatCost = 1,
    evolutionStage = EvolutionStage.STAGE_ONE) {

  override def isEvolutionOf(pokemon : PokemonCard) = pokemon.id == 7

}

private class WithdrawWortortle extends Move(
  "Withdraw",
  2,
  Map(EnergyType.WATER -> 1)) {

  override def perform(owner : Player, opp : Player) =
      setGeneralConditionChance(owner, opp, new WithdrawCondition())
}

private class WartortleBite extends Move(
  "Bite",
  3,
  Map(EnergyType.WATER -> 1)) {

  override def perform(owner : Player, opp : Player) = standardAttack(owner, opp, 40)

}

