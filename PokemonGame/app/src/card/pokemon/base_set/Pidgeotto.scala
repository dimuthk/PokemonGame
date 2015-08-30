package src.card.pokemon.base_set

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

class Pidgeotto extends StageOnePokemon(
    "Pidgeotto",
    "Pidgeotto-Base-Set-22.jpg",
    Deck.BASE_SET,
    Identifier.PIDGEOTTO,
    id = 17,
    maxHp = 60,
    firstMove = Some(new Whirlwind(
        "Whirlwind",
        ownerChooses = false,
        moveNum = 1,
        dmg = 20,
        totalEnergyReq = 2)),
    secondMove = Some(new MirrorMove(
        3)),
    energyType = EnergyType.COLORLESS,
    weakness = Some(EnergyType.THUNDER),
    resistance = Some(EnergyType.FIGHTING),
    retreatCost = 1) with MirrorMoveable