package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.Deck
import src.card.pokemon._

class Doduo extends BasicPokemon(
    "Doduo",
    "Doduo-Base-Set-48.jpg",
    Deck.BASE_SET,
    Identifier.DODUO,
    id = 84,
    maxHp = 50,
    firstMove = Some(new Move(
        "Fury Attack",
        1) {
        def perform = (owner, opp, args) => multipleHitAttack(owner, opp, 10, 2)
    }),
    energyType = EnergyType.COLORLESS,
    weakness = Some(EnergyType.THUNDER),
    resistance = Some(EnergyType.FIGHTING),
    retreatCost = 0)