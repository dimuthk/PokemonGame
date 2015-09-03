package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.Deck
import src.card.pokemon._

class Drowzee extends BasicPokemon(
    "Drowzee",
    "Drowzee-Base-Set-49.jpg",
    Deck.BASE_SET,
    Identifier.DROWZEE,
    id = 96,
    maxHp = 50,
    firstMove = Some(new Move(
        "Pound",
        1) {
        def perform = (owner, opp, args) => standardAttack(owner, opp, 10)
    }),
    secondMove = Some(new Move(
        "Confuse Ray",
        2,
        Map(EnergyType.PSYCHIC -> 2)) {
        def perform = (owner, opp, args) => confuseAttackChance(owner, opp, 10)
    }),
    energyType = EnergyType.PSYCHIC,
    weakness = Some(EnergyType.PSYCHIC),
    retreatCost = 1)