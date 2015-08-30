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

class Sandslash extends StageOnePokemon(
    "Sandslash",
    "Sandslash-Fossil-41.jpg",
    Deck.FOSSIL,
    Identifier.SANDSLASH,
    id = 28,
    maxHp = 70,
    firstMove = Some(new Move(
        "Slash",
        2) {
        def perform = (owner, opp) => standardAttack(owner, opp, 20)
        }),
    secondMove = Some(new Move(
      "Fury Swipes",
      2,
      Map(EnergyType.FIGHTING -> 2)) {
        def perform = (owner, opp) => multipleHitAttack(owner, opp, 20, 3)
      }),
    energyType = EnergyType.GRASS,
    weakness = Some(EnergyType.THUNDER),
    retreatCost = 1)