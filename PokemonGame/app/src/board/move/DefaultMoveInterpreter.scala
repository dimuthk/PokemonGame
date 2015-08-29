package src.board.move

import src.board.intermediary.IntermediaryRequest
import src.board.move.DefaultMoveInterpreter
import src.card.Card
import src.move.Move
import src.move.PokemonPower
import src.card.energy.EnergyCard
import src.card.pokemon.PokemonCard
import src.board.drag._
import src.player.Player

object DefaultMoveInterpreter extends MoveInterpreter {

    override def additionalRequest(owner : Player, opp : Player, command : MoveCommand) = command match {
      case AttackFromActive(moveNum : Int) => moveNum match {
        // There will never be an additional request for a pass
        case 3 => None
        case _ => owner.active.get.getMove(moveNum).get.additionalRequest(owner, opp)
      }  
      case AttackFromBench(i : Int, moveNum : Int) => owner.bench(i).get.getMove(moveNum).get.additionalRequest(owner, opp)
      case Intermediary(cmd : Seq[String]) => None
    }

    def attack(owner : Player, opp : Player, move : Move) : Unit = {
      move.perform(owner, opp)
      move match {
        case power : PokemonPower => ()
        case _ => flipTurn(owner, opp)
      }
    }

}
