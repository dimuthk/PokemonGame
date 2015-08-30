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

class Sandshrew extends BasicPokemon(
    "Sand-attack",
    "Sandshrew-Base-Set-62.jpg",
    Deck.BASE_SET,
    Identifier.SANDSHREW,
    id = 27,
    maxHp = 40,
    firstMove = Some(new Smokescreen(
      "Sand-attack",
      dmg = 10,
      1,
      Map(EnergyType.FIGHTING -> 1))),
    energyType = EnergyType.FIGHTING,
    weakness = Some(EnergyType.GRASS),
    resistance = Some(EnergyType.THUNDER),
    retreatCost = 1)