package src.card.pokemon.base_set

import src.card.Deck
import src.card.pokemon._
import src.json.Identifier
import src.move._
import src.move.MoveBuilder._
import src.move.ActivePokemonPower
import src.board.drag.CustomDragInterpreter
import src.player.Player
import src.card.energy.EnergyCard
import src.card.energy.EnergyType
import src.card.energy.WaterEnergy

class Pidgey extends BasicPokemon(
    "Pidgey",
    "Pidgey-Base-Set-57.jpg",
    Deck.BASE_SET,
    Identifier.PIDGEY,
    id = 16,
    maxHp = 40,
    firstMove = Some(new Whirlwind(
        "Whirlwind",
        2,
        10) {}),
    energyType = EnergyType.COLORLESS,
    weakness = Some(EnergyType.THUNDER),
    resistance = Some(EnergyType.FIGHTING),
    retreatCost = 1)