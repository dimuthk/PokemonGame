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


class Smokescreen(
    condName : String,
    dmg : Int,
    totalEnergyReq : Int,
    specialEnergyReq : Map[EnergyType.Value, Int] = Map()) extends Move(
      condName,
      totalEnergyReq,
      specialEnergyReq) {
        def perform = (owner, opp) => owner.active.get match {
          case _ : PokemonCard => {
            standardAttack(owner, opp, dmg)
            opp.active.get.smokescreen = true
            opp.active.get.generalCondition = Some(condName)
          }
          case _ => throw new Exception("Tried to use smokescreen with no card")
        }
      }