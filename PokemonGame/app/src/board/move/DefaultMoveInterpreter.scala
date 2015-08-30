package src.board.move

import src.move.MoveBuilder._
import src.board.intermediary.IntermediaryRequest
import src.board.move.DefaultMoveInterpreter
import src.card.Card
import src.move.Move
import src.move.PokemonPower
import src.card.energy.EnergyCard
import src.card.pokemon.PokemonCard
import src.board.drag._
import src.player.Player
import play.api.Logger

object DefaultMoveInterpreter extends MoveInterpreter {

    override def additionalRequest(owner : Player, opp : Player, command : MoveCommand) = command match {
      case AttackFromActive(moveNum : Int, args : Seq[String]) => moveNum match {
        // There will never be an additional request for a pass
        case 3 => None
        case _ => owner.active.get.getMove(moveNum).get.additionalRequest(owner, opp, args)
      }  
      case AttackFromBench(i : Int, moveNum : Int, args : Seq[String])
          => owner.bench(i).get.getMove(moveNum).get.additionalRequest(owner, opp, args)
    }

    def attackFromActive(owner : Player, opp : Player, move : Move, additional : Seq[String]) : Unit = move match {
      case power : PokemonPower => performMove(owner, opp, power, additional)
      case _ => {
        if (!owner.active.get.smokescreen || flippedHeads()) {
          performMove(owner, opp, move, additional)
        }
        flipTurn(owner, opp)
      }
    }

    private def performMove(owner : Player, opp : Player, move : Move, additional : Seq[String]) {
      if (additional.length == 0) {
        move.perform(owner, opp)
      } else {
        move.performWithAdditional(owner, opp, additional)
      }
    }

    def attackFromBench(owner : Player, opp : Player, move : Move, additional : Seq[String]) : Unit = move match {
      case power : PokemonPower => performMove(owner, opp, move, additional)
      case _ => throw new Exception("Non pokemon power move called from bench!")
    }

}
