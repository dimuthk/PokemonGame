package src.card.pokemon.base_set

import src.board.intermediary.IntermediaryRequest
import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.Deck
import src.card.pokemon._

class Diglett extends BasicPokemon(
    "Diglett",
    "Diglett-Base-Set-47.jpg",
    Deck.BASE_SET,
    Identifier.DIGLETT,
    id = 50,
    maxHp = 30,
    firstMove = Some(new Move(
        "Dig",
        1,
        Map(EnergyType.FIGHTING -> 1)) {
        def perform = (owner, opp, args) => standardAttack(owner, opp, 10)
    }),
    secondMove = Some(new Move(
        "Mud Slap",
        2,
        Map(EnergyType.FIGHTING -> 2)) {
            def perform = (owner, opp, args) => standardAttack(owner, opp, 30)
        }),
    energyType = EnergyType.FIGHTING,
    weakness = Some(EnergyType.GRASS),
    resistance = Some(EnergyType.THUNDER),
    retreatCost = 0)
