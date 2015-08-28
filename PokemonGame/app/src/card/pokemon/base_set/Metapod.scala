package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.board.move.PreventDamageInterpreter
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
    evolutionStage = EvolutionStage.STAGE_ONE)

private class Stiffen extends Move(
  "Stiffen",
  2,
  Map(),
  moveInterpreter = Some(new PreventDamageInterpreter())) {

  override def perform(owner : Player, opp : Player) {
    if (flippedHeads()) {
      moveInterpreter.get.isActive = true
      owner.active.get.generalCondition = Some("Stiffen")
    }
  }
}

private class StunSpore extends Move(
  "Stun Spore",
  2,
  Map(EnergyType.GRASS -> 2)) {

  override def perform(owner : Player, opp : Player) =
      paralyzeChanceAttack(owner, opp, 20)
}

