package src.move

import play.api.libs.json._
import src.board.move.MoveCommand
import src.move.MoveBuilder._
import src.card.pokemon.PokemonCard
import src.board.intermediary.IntermediaryRequest
import src.board.intermediary.ClickableCardRequest
import src.board.drag.CustomDragInterpreter
import src.board.move.CustomMoveInterpreter
import src.board.state.CustomStateGenerator
import src.card.energy.EnergyCard
import src.card.energy.EnergyType
import src.json.Identifier
import src.json.Jsonable
import src.player.Player


class Agility(
    condName : String,
    totalEnergyReq : Int,
    dmg : Int,
    specialEnergyReq : Map[EnergyType.Value, Int] = Map()) extends Move(
      condName,
      totalEnergyReq,
      specialEnergyReq) {
        def perform = (owner, opp) =>  {
          val active = owner.active.get
          if (flippedHeads()) {
            owner.notify(active.displayName + " successfully performed " + condName + "!")
            active.agility = true
            active.generalCondition = Some(condName)
          } else {
            owner.notify(condName + " failed")
          }
          standardAttack(owner, opp, dmg)
        }
    }