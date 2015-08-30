package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.Deck
import src.card.pokemon._

class Nidorino extends StageOnePokemon(
    "Nidorino",
    "Nidorino-Base-Set-37.jpg",
    Deck.BASE_SET,
    Identifier.NIDORINO,
    id = 33,
    maxHp = 60,
    firstMove = Some(new Move(
        "Double Kick",
        3,
        Map(EnergyType.GRASS -> 1)) {
        def perform = (owner, opp) => multipleHitAttack(owner, opp, 30, 2)
    }),
    secondMove = Some(new Move(
        "Horn Drill",
        4,
        Map(EnergyType.GRASS -> 2)) {
        def perform = (owner, opp) => standardAttack(owner, opp, 50)
        }),
    energyType = EnergyType.GRASS,
    weakness = Some(EnergyType.PSYCHIC),
    retreatCost = 1)
