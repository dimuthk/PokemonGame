package src.card.pokemon.fossil

import src.card.Deck
import src.move.Whirlwind
import src.move.Whirlwind._
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

class Moltres extends BasicPokemon(
    "Moltres",
    "Moltres-Fossil-12.jpg",
    Deck.FOSSIL,
    Identifier.MOLTRES,
    id = 146,
    maxHp = 70,
    // TODO wildfire
    secondMove = Some(new Move(
      "Dive Bomb",
      4,
      Map(EnergyType.FIRE -> 4)) {
        def perform = (owner, opp, args) => flippedHeads() match {
            case true => standardAttack(owner, opp, 80)
            case false => None
        }
      }),
    energyType = EnergyType.FIRE,
    resistance = Some(EnergyType.FIGHTING),
    retreatCost = 2)