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

class Grimer extends BasicPokemon(
    "Grimer",
    "Grimer-Fossil-48.jpg",
    Deck.FOSSIL,
    Identifier.GRIMER,
    id = 88,
    maxHp = 50,
    firstMove = Some(new Move(
      "Nasty Goo",
      1) {
        def perform = (owner, opp, args) => paralyzeAttackChance(owner, opp, 10)
      }),
    secondMove = Some(new Move(
      "Minimize",
      1,
      Map(EnergyType.GRASS -> 1)) {
        def perform = (owner, opp, args) => minimize(owner)
      }),
    energyType = EnergyType.GRASS,
    weakness = Some(EnergyType.PSYCHIC),
    retreatCost = 1)