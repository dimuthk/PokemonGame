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

class Graveler extends StageOnePokemon(
    "Graveler",
    "Graveler-Fossil-37.jpg",
    Deck.FOSSIL,
    Identifier.GRAVELER,
    id = 75,
    maxHp = 60,
    firstMove = Some(new Move(
      "Harden",
      2,
      Map(EnergyType.FIGHTING -> 2)) {
        def perform = (owner, opp, args) => harden(owner)
      }),
    secondMove = Some(new Move(
      "Rock Throw",
      3,
      Map(EnergyType.FIGHTING -> 2)) {
        def perform = (owner, opp, args) => standardAttack(owner, opp, 40)
      }),
    energyType = EnergyType.FIGHTING,
    weakness = Some(EnergyType.GRASS),
    retreatCost = 2)