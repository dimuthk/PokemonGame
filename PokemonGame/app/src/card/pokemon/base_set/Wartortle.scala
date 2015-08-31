package src.card.pokemon.base_set

import src.json.Identifier
import src.move._
import src.move.MoveBuilder._
import src.player.Player
import src.card.condition.PreventDamageCondition
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Wartortle extends BasicPokemon(
    "Wartortle",
    "Wartortle-Base-Set-42.jpg",
    Deck.BASE_SET,
    Identifier.IVYSAUR,
    id = 8,
    maxHp = 70,
    firstMove = Some(new Withdraw(
    "Withdraw",
    2,
    Map(EnergyType.WATER -> 1))),
    secondMove = Some(new Move(
      "Bite",
      3,
      Map(EnergyType.WATER -> 1)) {
        def perform = (owner, opp, args) => standardAttack(owner, opp, 40)
      }),
    energyType = EnergyType.WATER,
    weakness = Some(EnergyType.THUNDER),
    retreatCost = 1)