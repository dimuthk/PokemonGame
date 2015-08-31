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

class Zubat extends BasicPokemon(
    "Zubat",
    "Zubat-Fossil-57.jpg",
    Deck.FOSSIL,
    Identifier.ZUBAT,
    id = 41,
    maxHp = 40,
    firstMove = Some(new Move(
      "Supersonic",
      2) {
        def perform = (owner, opp, args) => confuseAttackChance(owner, opp)
      }),
    secondMove = Some(new Move(
      "Leech Life",
      2,
      Map(EnergyType.GRASS -> 1)) {
        def perform = (owner, opp, args) => energyDrainAttack(owner, opp, 10)
      }),
    energyType = EnergyType.GRASS,
    weakness = Some(EnergyType.PSYCHIC),
    resistance = Some(EnergyType.FIGHTING),
    retreatCost = 0)