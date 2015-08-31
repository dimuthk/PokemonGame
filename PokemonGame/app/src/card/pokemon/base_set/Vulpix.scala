package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.condition.PreventDamageCondition
import src.card.pokemon._
import src.card.Deck

class Vulpix extends BasicPokemon(
    "Vulpix",
    "Vulpix-Base-Set-68.jpg",
    Deck.BASE_SET,
    Identifier.VULPIX,
    id = 37,
    maxHp = 50,
    firstMove = Some(new Move(
      "Confuse Ray",
      2,
      Map(EnergyType.FIRE -> 2)) {
        def perform = (owner, opp, args) => confuseAttackChance(owner, opp, 10)
      }),
    energyType = EnergyType.FIRE,
    weakness = Some(EnergyType.WATER),
    retreatCost = 1)