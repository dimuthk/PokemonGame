package src.card.pokemon.jungle

import src.json.Identifier
import src.move._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.board.move.DefaultMoveInterpreter
import src.board.move.CustomMoveInterpreter
import src.board.intermediary.IntermediaryRequest
import src.board.intermediary.ClickableCardRequest
import src.card.condition.PreventDamageCondition
import src.card.pokemon._
import src.card.Deck

import play.api.Logger

class Butterfree extends BasicPokemon(
    "Butterfree",
    "Butterfree-Jungle-33.jpg",
    Deck.JUNGLE,
    Identifier.BUTTERFREE,
    id = 12,
    maxHp = 70,
    firstMove = Some(new Whirlwind(
        "Whirlwind",
        ownerChooses = false,
        moveNum = 1,
        dmg = 20,
        totalEnergyReq = 2)),
    secondMove = Some(new Move(
      "Mega Drain",
      4,
      Map(EnergyType.GRASS -> 4)) {
        def perform = (owner, opp) => {
          val dmg = opp.active.get.calculateDmg(owner.active.get, 40)
          standardAttack(owner, opp, 40)
          owner.active.get.heal(roundUp(dmg / 2))
        }
    }),
    energyType = EnergyType.GRASS,
    weakness = Some(EnergyType.FIRE),
    resistance = Some(EnergyType.FIGHTING),
    retreatCost = 0)
