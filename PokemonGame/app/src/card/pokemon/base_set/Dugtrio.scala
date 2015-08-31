package src.card.pokemon.base_set

import src.board.intermediary.IntermediaryRequest
import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.Deck
import src.card.pokemon._

class Dugtrio extends BasicPokemon(
    "Dugtrio",
    "Dugtrio-Base-Set-19.jpg",
    Deck.BASE_SET,
    Identifier.DUGTRIO,
    id = 51,
    maxHp = 70,
    firstMove = Some(new Move(
        "Slash",
        3,
        Map(EnergyType.FIGHTING -> 2)) {
        def perform = (owner, opp, args) => standardAttack(owner, opp, 40)
    }),
    secondMove = Some(new Move(
        "Earthquake",
        4,
        Map(EnergyType.FIGHTING -> 4)) {
            def perform = (owner, opp, args) => {
                for (pc : PokemonCard <- owner.bench.toList.flatten) {
                    pc.takeDamage(10)
                }
                standardAttack(owner, opp, 70)
            }
        }),
    energyType = EnergyType.FIGHTING,
    weakness = Some(EnergyType.GRASS),
    resistance = Some(EnergyType.THUNDER),
    retreatCost = 2)
