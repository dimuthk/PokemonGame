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


class MirrorMove(
  totalEnergyReq : Int,
  specialEnergyReq : Map[EnergyType.Value, Int] = Map()) extends Move(
  "Mirror Move",
    totalEnergyReq,
    specialEnergyReq) {

    def perform = (owner, opp) => owner.active.get match {
      case pc : PokemonCard => pc.lastAttack match {
        case -1 => throw new Exception("Tried to use mirror move when no previous attack")
        case _ => standardAttack(owner, opp, pc.lastAttack)
      }
      case _ => throw new Exception("Tried to use mirror move with a non pokemon card")
    }

    override def update(owner : Player, opp : Player, pc : PokemonCard, turnSwapped : Boolean, isActive : Boolean) : Unit = {
      super.update(owner, opp, pc, turnSwapped, isActive)
      // The move is disabled if no attack was made previously.
      pc.lastAttack match {
        case -1 => status = Status.DISABLED
        case _ => ()
      }
    }

  }