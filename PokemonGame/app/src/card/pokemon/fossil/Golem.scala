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

class Golem extends StageTwoPokemon(
    "Golem",
    "Golem-Fossil-36.jpg",
    Deck.FOSSIL,
    Identifier.GOLEM,
    id = 76,
    maxHp = 80,
    firstMove = Some(new Move(
      "Avalanche",
      4,
      Map(EnergyType.FIGHTING -> 3)) {
        def perform = (owner, opp, args) => standardAttack(owner, opp, 60)
      }),
    secondMove = Some(new Move(
      "Selfdestruct",
      4,
      Map(EnergyType.FIGHTING -> 4)) {
        def perform = (owner, opp, args) => selfDestruct(owner, opp, 100, 100, 20)
      }),
    energyType = EnergyType.FIGHTING,
    weakness = Some(EnergyType.GRASS),
    retreatCost = 4)