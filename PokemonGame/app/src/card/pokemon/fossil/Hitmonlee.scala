package src.card.pokemon.fossil

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

class Hitmonlee extends BasicPokemon(
    "Hitmonlee",
    "Hitmonlee-Fossil-7.jpg",
    Deck.FOSSIL,
    Identifier.HITMONLEE,
    id = 106,
    maxHp = 60,
    firstMove = Some(new BenchSelectAttack(
      "Stretch Kick",
      mainDmg = 0,
      benchDmg = 20,
      numBenchSelects = 1,
      2,
      Map(EnergyType.FIGHTING -> 2))),
    secondMove = Some(new Move(
      "High Jump Kick",
      3,
      Map(EnergyType.FIGHTING -> 3)) {
        def perform = (owner, opp, args) => standardAttack(owner, opp, 50)
      }),
    energyType = EnergyType.FIGHTING,
    weakness = Some(EnergyType.PSYCHIC),
    retreatCost = 1)