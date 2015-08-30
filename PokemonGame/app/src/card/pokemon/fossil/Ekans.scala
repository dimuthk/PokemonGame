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

class Ekans extends BasicPokemon(
    "Ekans",
    "Ekans-Fossil-46.jpg",
    Deck.FOSSIL,
    Identifier.EKANS,
    id = 23,
    maxHp = 40,
    firstMove = Some(new Move(
      "Spit Poison",
      1,
      Map(EnergyType.GRASS -> 1)) {
        def perform = (owner, opp) => poisonChanceAttack(owner, opp, 0)
      }),
    secondMove = Some(new Move(
      "Wrap",
      2,
      Map(EnergyType.GRASS -> 1)) {
        def perform = (owner, opp) => paralyzeChanceAttack(owner, opp, 20)
      }),
    energyType = EnergyType.GRASS,
    weakness = Some(EnergyType.PSYCHIC),
    retreatCost = 1)