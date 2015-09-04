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

class Weezing extends BasicPokemon(
    "Weezing",
    "Weezing-Fossil-45.jpg",
    Deck.FOSSIL,
    Identifier.WEEZING,
    id = 110,
    maxHp = 60,
    firstMove = Some(new Move(
      "Smog",
      2,
      Map(EnergyType.GRASS -> 2)) {
        def perform = (owner, opp, args) => poisonAttackChance(owner, opp, 20)
      }),
    secondMove = Some(new Move(
      "Selfdestruct",
      3,
      Map(EnergyType.GRASS -> 2)) {
        def perform = (owner, opp, args) => selfDestruct(owner, opp, 60, 60, 10)
      }),
    energyType = EnergyType.GRASS,
    weakness = Some(EnergyType.PSYCHIC),
    retreatCost = 1)