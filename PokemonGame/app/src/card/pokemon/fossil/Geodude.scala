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

class Geodude extends BasicPokemon(
    "Geodude",
    "Geodude-Fossil-47.jpg",
    Deck.FOSSIL,
    Identifier.GEODUDE,
    id = 74,
    maxHp = 50,
    firstMove = Some(new Move(
      "Stone Barrage",
      2,
      Map(EnergyType.FIGHTING -> 1)) {
        def perform = (owner, opp, args) => {
          var dmg = 0
          while (flippedHeads()) {
            dmg = dmg + 10
          }
          standardAttack(owner, opp, dmg)
        }
      }),
    energyType = EnergyType.FIGHTING,
    weakness = Some(EnergyType.GRASS),
    retreatCost = 1)