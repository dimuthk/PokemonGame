package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.Whirlwind
import src.move.Whirlwind._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.condition.PreventDamageCondition
import src.card.pokemon._
import src.card.Deck

class Ninetales extends StageOnePokemon(
    "Ninetales",
    "Ninetales-Base-Set-12.jpg",
    Deck.BASE_SET,
    Identifier.NINETALES,
    id = 38,
    maxHp = 80,
    firstMove = Some(new Whirlwind(
        "Lure",
        ownerChooses = true,
        moveNum = 1,
        dmg = 20,
        totalEnergyReq = 2)),
    secondMove = Some(new Move(
      "Fire Blast",
      4,
      Map(EnergyType.FIRE -> 4)) {
        def perform = (owner, opp) => energyDiscardAttack(owner, opp, 80, EnergyType.FIRE)
      }),
    energyType = EnergyType.FIRE,
    weakness = Some(EnergyType.WATER),
    retreatCost = 1)