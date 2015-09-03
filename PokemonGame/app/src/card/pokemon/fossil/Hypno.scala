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

class Hypno extends StageOnePokemon(
    "Hypno",
    "Hypno-Fossil-8.jpg",
    Deck.FOSSIL,
    Identifier.HYPNO,
    id = 97,
    maxHp = 90,
    // ugh. TODO
    firstMove = Some(new Move(
      "Prophecy",
      1) {
        def perform = (owner, opp, args) => paralyzeAttackChance(owner, opp, 10)
      }),
    secondMove = Some(new BenchSelectAttack(
      "Dark Mind",
      mainDmg = 30,
      benchDmg = 10,
      numBenchSelects = 1,
      3,
      Map(EnergyType.PSYCHIC -> 3))),
    energyType = EnergyType.PSYCHIC,
    weakness = Some(EnergyType.PSYCHIC),
    retreatCost = 2)