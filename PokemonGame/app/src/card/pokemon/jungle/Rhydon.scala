package src.card.pokemon.jungle

import src.json.Identifier
import src.move._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.board.intermediary.IntermediaryRequest
import src.board.intermediary.ClickableCardRequest
import src.card.condition.PreventDamageCondition
import src.card.pokemon._
import src.card.Deck

import play.api.Logger

class Rhydon extends StageOnePokemon(
    "Rhydon",
    "Rhydon-Jungle-45.jpg",
    Deck.JUNGLE,
    Identifier.RHYDON,
    id = 112,
    maxHp = 100,
    firstMove = Some(new Move(
      "Horn Attack",
      3,
      Map(EnergyType.FIGHTING -> 1)) {
        def perform = (owner, opp, args) => standardAttack(owner, opp, 30)
      }),
    secondMove = Some(new Whirlwind(
        "Ram",
        ownerChooses = false,
        dmg = 50,
        totalEnergyReq = 4,
        specialEnergyReq = Map(EnergyType.FIGHTING -> 4),
        selfDmg = 20)),
    energyType = EnergyType.FIGHTING,
    weakness = Some(EnergyType.GRASS),
    resistance = Some(EnergyType.THUNDER),
    retreatCost = 3)
