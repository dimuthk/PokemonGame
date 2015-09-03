package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.Deck
import src.card.pokemon._

class Voltorb extends BasicPokemon(
    "Voltorb",
    "Voltorb-Base-Set-67.jpg",
    Deck.BASE_SET,
    Identifier.VOLTORB,
    id = 100,
    maxHp = 40,
    firstMove = Some(new Move(
        "Tackle",
        1) {
        def perform = (owner, opp, args) => standardAttack(owner, opp, 10)
    }),
    energyType = EnergyType.THUNDER,
    weakness = Some(EnergyType.FIGHTING),
    retreatCost = 1)