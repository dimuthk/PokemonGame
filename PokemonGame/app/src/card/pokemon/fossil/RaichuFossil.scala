package src.card.pokemon.fossil

import src.card.Deck
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
import src.move.BenchSelectAttack

class RaichuFossil extends StageOnePokemon(
    "Raichu",
    "Raichu-Fossil-14.jpg",
    Deck.FOSSIL,
    Identifier.RAICHU_FOSSIL,
    id = 26,
    maxHp = 90,
    firstMove = Some(new BenchSelectAttack(
      "Gigashock",
      mainDmg = 30,
      benchDmg = 10,
      numBenchSelects = 3,
      4,
      Map(EnergyType.THUNDER -> 4)) {}),
    energyType = EnergyType.THUNDER,
    weakness = Some(EnergyType.FIGHTING),
    retreatCost = 1)