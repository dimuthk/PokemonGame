package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.Deck
import src.card.pokemon._

class Electrode extends StageOnePokemon(
    "Electrode",
    "Electrode-Base-Set-21.jpg",
    Deck.BASE_SET,
    Identifier.ELECTRODE,
    id = 101,
    maxHp = 80,
    // TODO buzzap
    secondMove = Some(new Move(
        "Electric Shock",
        3,
        Map(EnergyType.THUNDER -> 3)) {
            def perform = (owner, opp, args) => selfDamageChanceAttack(owner, opp, 50, 10)
        }),
    energyType = EnergyType.THUNDER,
    weakness = Some(EnergyType.FIGHTING),
    retreatCost = 1)