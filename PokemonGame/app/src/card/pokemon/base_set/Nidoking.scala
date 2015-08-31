package src.card.pokemon.base_set

import src.board.intermediary.IntermediaryRequest
import src.json.Identifier
import src.move.Move
import src.card.condition.PoisonStatus
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.Deck
import src.card.pokemon._

class Nidoking extends StageTwoPokemon(
    "Nidoking",
    "Nidoking-Base-Set-11.jpg",
    Deck.BASE_SET,
    Identifier.NIDOKING,
    id = 34,
    maxHp = 90,
    firstMove = Some(new Move(
        "Thrash",
        3,
        Map(EnergyType.GRASS -> 1)) {
        def perform = (owner, opp, args) => extraDamageOrHurtSelf(owner, opp, 30, 10)
    }),
    secondMove = Some(new Move(
        "Toxic",
        3,
        Map(EnergyType.GRASS -> 3)) {
            def perform = (owner, opp, args) => {
                opp.active.get.poisonStatus = Some(PoisonStatus.TOXIC)
                standardAttack(owner, opp, 20)
            }
    }),
    energyType = EnergyType.GRASS,
    weakness = Some(EnergyType.PSYCHIC),
    retreatCost = 3)
