package src.board.move

import src.board.intermediary.IntermediaryRequest
import src.card.Card
import src.move.Move
import src.move.MoveBuilder._
import src.card.energy.EnergyCard
import src.card.pokemon.PokemonCard
import src.move.PokemonPower
import src.board.drag._
import src.player.Player

object DefaultMoveInterpreter extends MoveInterpreter {

    def attackFromActive = (owner, opp, move, args) => move match {
      case power : PokemonPower => power.perform(owner, opp, args)
      case _ => {
        val intermediary = (!owner.active.get.smokescreen || flippedHeads()) match {
          case true => move.perform(owner, opp, args)
          case false => None
        }
        flipTurn(owner, opp)
        intermediary
      }
    }

    def attackFromBench = (owner, opp, move, args) => move match {
      case power : PokemonPower => power.perform(owner, opp, args)
      case _ => throw new Exception("Non pokemon power move called from bench!")
    }

}
