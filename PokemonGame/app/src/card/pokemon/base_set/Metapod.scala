package src.card.pokemon

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.condition.PreventDamageCondition
import src.card.pokemon._
import src.card.Deck

class Metapod extends PokemonCard(
    "Metapod",
    "Metapod-Base-Set-54.jpg",
    Deck.BASE_SET,
    Identifier.METAPOD,
    id = 11,
    maxHp = 70,
    firstMove = Some(new Stiffen()),
    secondMove = Some(new StunSpore()),
    energyType = EnergyType.GRASS,
    weakness = Some(EnergyType.FIRE),
    retreatCost = 2,
    evolutionStage = EvolutionStage.STAGE_ONE) {

  override def isEvolutionOf(pokemon : PokemonCard) = pokemon.id == 10

}

private class Stiffen extends Move(
  "Stiffen",
  2,
  Map()) {

  override def perform(owner : Player, opp : Player) =
      setGeneralConditionChance(owner, opp, new PreventDamageCondition("Stiffen"))
}

private class StunSpore extends Move(
  "Stun Spore",
  2,
  Map(EnergyType.GRASS -> 2)) {

  override def perform(owner : Player, opp : Player) =
      paralyzeChanceAttack(owner, opp, 20)
}

