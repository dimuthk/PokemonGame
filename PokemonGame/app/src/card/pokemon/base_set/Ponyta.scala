package src.card.pokemon.base_set

import src.board.intermediary.IntermediaryRequest
import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.Deck
import src.card.pokemon._

class Ponyta extends BasicPokemon(
    "Ponyta",
    "Ponyta-Base-Set-60.jpg",
    Deck.BASE_SET,
    Identifier.PONYTA,
    id = 77,
    maxHp = 40,
    firstMove = Some(new Move(
        "Smash Kick",
        2) {
        def perform = (owner, opp, args) => standardAttack(owner, opp, 20)
    }),
    secondMove = Some(new Move(
        "Flame Tail",
        2,
        Map(EnergyType.FIRE -> 2)) {
            def perform = (owner, opp, args) => standardAttack(owner, opp, 30)
        }),
    energyType = EnergyType.FIRE,
    weakness = Some(EnergyType.WATER),
    retreatCost = 1)
