package src.card.pokemon.base_set

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

class Beedrill extends StageTwoPokemon(
    "Beedrill",
    "Beedrill-Base-Set-17.jpg",
    Deck.BASE_SET,
    Identifier.BEEDRILL,
    id = 15,
    maxHp = 80,
    firstMove = Some(new Move(
      "Twineedle",
      3) {
        def perform = (owner, opp) => multipleHitAttack(owner, opp, 30, 2)
      }),
    secondMove = Some(new Move(
      "Poison String",
      3,
      Map(EnergyType.GRASS -> 3)) {
        def perform = (owner, opp) => poisonChanceAttack(owner, opp, 40)
      }),
    energyType = EnergyType.GRASS,
    weakness = Some(EnergyType.FIRE),
    resistance = Some(EnergyType.FIGHTING),
    retreatCost = 0)