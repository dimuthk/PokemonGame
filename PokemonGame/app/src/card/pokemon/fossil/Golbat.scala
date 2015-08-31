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

class Golbat extends StageOnePokemon(
    "Golbat",
    "Golbat-Fossil-34.jpg",
    Deck.FOSSIL,
    Identifier.GOLBAT,
    id = 42,
    maxHp = 60,
    firstMove = Some(new Move(
      "Wing Attack",
      3) {
        def perform = (owner, opp, args) => standardAttack(owner, opp, 30)
      }),
    secondMove = Some(new Move(
      "Leech Life",
      3,
      Map(EnergyType.GRASS -> 2)) {
        def perform = (owner, opp, args) => healthDrainAttack(owner, opp, 20)
      }),
    energyType = EnergyType.GRASS,
    weakness = Some(EnergyType.PSYCHIC),
    resistance = Some(EnergyType.FIGHTING),
    retreatCost = 0)