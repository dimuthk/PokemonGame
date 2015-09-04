package src.card.pokemon.base_set

import src.board.intermediary.IntermediaryRequest
import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.Deck
import src.card.pokemon._

class Tangela extends BasicPokemon(
    "Tangela",
    "Tangela-Base-Set-66.jpg",
    Deck.BASE_SET,
    Identifier.TANGELA,
    id = 114,
    maxHp = 50,
    firstMove = Some(new Move(
        "Bind",
        2,
        Map(EnergyType.GRASS -> 1)) {
        def perform = (owner, opp, args) => paralyzeAttackChance(owner, opp, 20)
    }),
    secondMove = Some(new Move(
        "Poisonpowder",
        3,
        Map(EnergyType.GRASS -> 3)) {
        def perform = (owner, opp, args) => poisonAttack(owner, opp, 20)
        }),
    energyType = EnergyType.GRASS,
    weakness = Some(EnergyType.FIRE),
    retreatCost = 2)
