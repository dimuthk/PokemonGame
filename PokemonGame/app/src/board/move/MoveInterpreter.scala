package src.board.move

import src.board.intermediary.IntermediaryRequest
import src.card.Card
import src.move.Move
import src.card.energy.EnergyCard
import src.card.pokemon.PokemonCard
import src.board.drag._
import src.player.Player

abstract class MoveInterpreter {

    def additionalRequest(owner : Player, opp : Player, command : MoveCommand) : Option[IntermediaryRequest] = None

    def handleIntermediary(cmds : Seq[String]) : Unit = ()

    def attack(owner : Player, opp : Player, move : Move) : Unit

    def handleMove(owner : Player, opp : Player, cmd : MoveCommand) = cmd match {
        case AttackFromActive(moveNum : Int) => moveNum match {
            case 1 => attack(owner, opp, owner.active.get.firstMove.get)
            case 2 => attack(owner, opp, owner.active.get.secondMove.get)
            case _ => throw new Exception("Invalid move number")
        }
        case AttackFromBench(benchIndex : Int, moveNum : Int) => (owner.bench(benchIndex).get, moveNum) match {
            case (bc : PokemonCard, 1) => attack(owner, opp, bc.firstMove.get)
            case (bc : PokemonCard, 2) => attack(owner, opp, bc.secondMove.get)
            case _ => throw new Exception("Invalid move number")
        }
        case Intermediary(cmds : Seq[String]) => handleIntermediary(cmds)
    }

}
