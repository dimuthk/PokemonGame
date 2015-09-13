package src.card.pokemon.base_set

import src.board.intermediary.IntermediaryRequest
import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.Deck
import src.card.pokemon._

class Bulbasaur extends BasicPokemon(
    "Bulbasaur",
    "Bulbasaur-Base-Set-44.jpg",
    Deck.BASE_SET,
    Identifier.BULBASAUR,
    id = 1,
    maxHp = 40,
    firstMove = Some(new Move(
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
    resistance = None,
    retreatCost = 1)
