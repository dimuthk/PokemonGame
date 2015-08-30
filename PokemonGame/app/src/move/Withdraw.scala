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


class Withdraw(
    condName : String,
    totalEnergyReq : Int,
    specialEnergyReq : Map[EnergyType.Value, Int] = Map()) extends Move(
      condName,
      totalEnergyReq,
      specialEnergyReq) {
        def perform = (owner, opp) => {
            val active = owner.active.get
            flippedHeads() match {
                case true => {
                    owner.notify(active.displayName + " successfully performed " + condName + "!")
                    active.withdrawn = true
                    active.generalCondition = Some(condName)
                }
                case false => owner.notify(condName + " failed")
            }
        }
      }