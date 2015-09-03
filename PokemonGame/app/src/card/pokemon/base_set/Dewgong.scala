package src.card.pokemon.base_set

import src.board.intermediary.IntermediaryRequest
import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.Deck
import src.card.pokemon._

class Dewgong extends StageOnePokemon(
    "Dewgong",
    "Dewgong-Base-Set-25.jpg",
    Deck.BASE_SET,
    Identifier.DEWGONG,
    id = 87,
    maxHp = 80,
    firstMove = Some(new Move(
        "Headbutt",
        3,
        Map(EnergyType.WATER -> 2)) {
        def perform = (owner, opp, args) => standardAttack(owner, opp, 50)
    }),
    secondMove = Some(new Move(
        "Ice Beam",
        4,
        Map(EnergyType.WATER -> 2)) {
        def perform = (owner, opp, args) => paralyzeAttackChance(owner, opp, 30)
    }),
    energyType = EnergyType.WATER,
    weakness = Some(EnergyType.THUNDER),
    retreatCost = 3)
