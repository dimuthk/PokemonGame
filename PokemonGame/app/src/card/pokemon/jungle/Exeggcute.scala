package src.card.pokemon.base_set

import src.board.intermediary.IntermediaryRequest
import src.json.Identifier
import src.move._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.Deck
import src.card.pokemon._

class Exeggcute extends BasicPokemon(
    "Exeggcute",
    "Exeggcute-Jungle-52.jpg",
    Deck.JUNGLE,
    Identifier.EXEGGCUTE,
    id = 102,
    maxHp = 50,
    firstMove = Some(new Whirlwind(
        "Teleport",
        ownerChooses = true,
        dmg = 0,
        totalEnergyReq = 1,
        Map(EnergyType.PSYCHIC -> 1),
        switchOwner = true)),
    secondMove = Some(new Move(
        "Leech Seed",
        2,
        Map(EnergyType.GRASS -> 2)) {
        def perform = (owner, opp, args) => {
            if (opp.active.get.takeDamage(owner.active, 20) > 0) {
                owner.active.get.heal(10)
            }
        }
    }),
    energyType = EnergyType.GRASS,
    weakness = Some(EnergyType.FIRE),
    retreatCost = 1)
