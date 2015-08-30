package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.Deck
import src.card.pokemon._

class NidoranMale extends BasicPokemon(
    "Nidoran",
    "Nidoran-M-Base-Set-55.jpg",
    Deck.BASE_SET,
    Identifier.NIDORAN_MALE,
    id = 32,
    maxHp = 40,
    firstMove = Some(new Move(
        "Horn Hazard",
        1,
        Map(EnergyType.GRASS -> 1)) {
        def perform = (owner, opp) => multipleHitAttack(owner, opp, 30, 1)
    }),
    energyType = EnergyType.GRASS,
    weakness = Some(EnergyType.PSYCHIC),
    retreatCost = 1)
