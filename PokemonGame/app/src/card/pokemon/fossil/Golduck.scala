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

class Golduck extends BasicPokemon(
    "Golduck",
    "Golduck-Fossil-35.jpg",
    Deck.FOSSIL,
    Identifier.GOLDUCK,
    id = 54,
    maxHp = 70,
    firstMove = Some(new Move(
      "Psyshock",
      1,
      Map(EnergyType.PSYCHIC -> 1)) {
        def perform = (owner, opp, args) => paralyzeAttackChance(owner, opp, 10)
      }),
    secondMove = Some(new HyperBeam(
      "Hyper Beam",
      dmg = 20,
      3,
      Map(EnergyType.WATER -> 2))),
    energyType = EnergyType.WATER,
    weakness = Some(EnergyType.THUNDER),
    retreatCost = 1)