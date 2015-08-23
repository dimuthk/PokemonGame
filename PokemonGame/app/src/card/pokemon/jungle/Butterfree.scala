package src.card.pokemon.jungle

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.condition.PreventDamageCondition
import src.card.pokemon._
import src.card.Deck

class Butterfree extends PokemonCard(
    "Butterfree",
    "Butterfree-Jungle-33.jpg",
    Deck.JUNGLE,
    Identifier.BUTTERFREE,
    id = 12,
    maxHp = 70,
    firstMove = None,
    secondMove = None,
    energyType = EnergyType.GRASS,
    weakness = Some(EnergyType.FIRE),
    resistance = Some(EnergyType.FIGHTING),
    retreatCost = 0,
    evolutionStage = EvolutionStage.STAGE_TWO) {

  override def isEvolutionOf(pokemon : PokemonCard) = pokemon.id == 11

}