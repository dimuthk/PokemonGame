package src.card.pokemon.base_set

import src.board.intermediary.IntermediaryRequest
import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.Deck
import src.card.pokemon._

class Exeggutor extends StageOnePokemon(
    "Exeggutor",
    "Exeggutor-Jungle-35.jpg",
    Deck.JUNGLE,
    Identifier.EXEGGUTOR,
    id = 103,
    maxHp = 80,
    firstMove = Some(new Move(
        "Teleport",
        1,
        Map(EnergyType.PSYCHIC -> 1)) {
            def perform = (owner, opp, args) => sleepAttack(owner, opp)
        }),
    secondMove = Some(new Move(
        "Big Eggsplosion",
        1) {
        def perform = (owner, opp, args) => {
            var dmg = 0
            for (_ <- 0 until owner.active.get.energyCards.length) {
                if (flippedHeads()) {
                    dmg += 20
                }
            }
            standardAttack(owner, opp, dmg)
        }
    }),
    energyType = EnergyType.GRASS,
    weakness = Some(EnergyType.FIRE),
    retreatCost = 3)
