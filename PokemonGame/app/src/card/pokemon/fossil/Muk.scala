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

class Muk extends BasicPokemon(
    "Muk",
    "Muk-Fossil-13.jpg",
    Deck.FOSSIL,
    Identifier.MUK,
    id = 89,
    maxHp = 70,
    // TODO ...
    firstMove = Some(new PassivePokemonPower("Toxic Gas") {}),
    secondMove = Some(new Move(
      "Sludge",
      3,
      Map(EnergyType.GRASS -> 3)) {
        def perform = (owner, opp, args) => poisonAttackChance(owner, opp, 30)
      }),
    energyType = EnergyType.GRASS,
    weakness = Some(EnergyType.PSYCHIC),
    retreatCost = 2)