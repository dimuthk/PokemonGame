package src.card.pokemon.base_set

import src.board.intermediary.IntermediaryRequest
import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.Deck
import src.card.pokemon._

class Seel extends BasicPokemon(
    "Seel",
    "Seel-Base-Set-41.jpg",
    Deck.BASE_SET,
    Identifier.SEEL,
    id = 86,
    maxHp = 60,
    firstMove = Some(new Move(
        "Headbutt",
        1,
        Map(EnergyType.WATER -> 1)) {
        def perform = (owner, opp, args) => standardAttack(owner, opp, 10)
    }),
    energyType = EnergyType.WATER,
    weakness = Some(EnergyType.THUNDER),
    retreatCost = 1)
