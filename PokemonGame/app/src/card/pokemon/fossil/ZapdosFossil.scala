package src.card.pokemon.fossil

import src.card.Deck
import src.move.Whirlwind
import src.move.Whirlwind._
import src.card.pokemon._
import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.move.ActivePokemonPower
import src.board.drag.CustomDragInterpreter
import src.player.Player
import src.card.energy.EnergyCard
import src.card.energy.EnergyType
import src.card.energy.WaterEnergy

class ZapdosFossil extends StageOnePokemon(
    "Zapdos",
    "Zapdos-Fossil-15.jpg",
    Deck.FOSSIL,
    Identifier.ZAPDOS_FOSSIL,
    id = 145,
    maxHp = 80,
    firstMove = Some(new Move(
        "Thunderstorm",
        4,
        Map(EnergyType.THUNDER -> 4)) {
            def perform = (owner, opp, args) => {
                for (bc : PokemonCard <- opp.bench.toList.flatten) {
                    if (flippedHeads()) {
                        bc.takeDamage(owner.active, 20)
                    } else {
                        owner.active.get.takeDamage(None, 10)
                    }
                }
                standardAttack(owner, opp, 40)
            }
        }),
    energyType = EnergyType.THUNDER,
    resistance = Some(EnergyType.FIGHTING),
    retreatCost = 2)