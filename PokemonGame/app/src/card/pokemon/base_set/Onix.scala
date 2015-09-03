package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.Deck
import src.card.pokemon._

class Onix extends BasicPokemon(
    "Onix",
    "Onix-Base-Set-56.jpg",
    Deck.BASE_SET,
    Identifier.ONIX,
    id = 95,
    maxHp = 90,
    firstMove = Some(new Move(
        "Rock Throw",
        1,
        Map(EnergyType.FIGHTING -> 1)) {
        def perform = (owner, opp, args) => standardAttack(owner, opp, 10)
    }),
    secondMove = Some(new Move(
        "Harden",
        2,
        Map(EnergyType.FIGHTING -> 2)) {
        def perform = (owner, opp, args) => harden(owner)
    }),
    energyType = EnergyType.FIGHTING,
    weakness = Some(EnergyType.GRASS),
    retreatCost = 3)