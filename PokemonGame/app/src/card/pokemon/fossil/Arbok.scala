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

class Arbok extends StageOnePokemon(
    "Arbok",
    "Arbok-Fossil-31.jpg",
    Deck.FOSSIL,
    Identifier.ARBOK,
    id = 24,
    maxHp = 60,
    firstMove = Some(new Whirlwind(
        "Terror Strike",
        ownerChooses = false,
        moveNum = 1,
        dmg = 10,
        onFlip = true,
        totalEnergyReq = 1,
        specialEnergyReq = Map(EnergyType.GRASS -> 1))),
    secondMove = Some(new Move(
      "Wrap",
      3,
      Map(EnergyType.GRASS -> 2)) {
        def perform = (owner, opp) => poisonAttack(owner, opp, 20)
      }),
    energyType = EnergyType.GRASS,
    weakness = Some(EnergyType.PSYCHIC),
    retreatCost = 2)