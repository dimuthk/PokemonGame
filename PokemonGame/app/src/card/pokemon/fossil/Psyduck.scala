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

class Psyduck extends BasicPokemon(
    "Psyduck",
    "Psyduck-Fossil-53.jpg",
    Deck.FOSSIL,
    Identifier.PSYDUCK,
    id = 53,
    maxHp = 50,
    firstMove = Some(new Move(
      "Headache",
      1,
      Map(EnergyType.PSYCHIC -> 1)) {
        def perform = (owner, opp, args) => {
          owner.trainerBan = true
          None
        }
      }),
    secondMove = Some(new Move(
      "Fury Swipes",
      1,
      Map(EnergyType.WATER -> 1)) {
        def perform = (owner, opp, args) => multipleHitAttack(owner, opp, 10, 3)
      }),
    energyType = EnergyType.WATER,
    weakness = Some(EnergyType.THUNDER),
    retreatCost = 1)