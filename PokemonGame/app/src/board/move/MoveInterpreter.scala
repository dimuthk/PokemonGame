package src.board.move

import src.board.intermediary.IntermediaryRequest
import src.card.Card
import src.move.Move
import src.card.energy.EnergyCard
import src.card.pokemon.PokemonCard
import src.board.drag._
import src.player.Player

abstract class MoveInterpreter {

    //def additionalRequest(owner : Player, opp : Player, command : MoveCommand) : Option[IntermediaryRequest] = None

    def attackFromActive(owner : Player, opp : Player, move : Move, additional : Seq[String]) : Option[IntermediaryRequest]

    def attackFromBench(owner : Player, opp : Player, move : Move, additional : Seq[String]) : Option[IntermediaryRequest]

    def handleMove(owner : Player, opp : Player, cmd : MoveCommand) = cmd match {
        case AttackFromActive(moveNum : Int, additional : Seq[String]) => moveNum match {
            case 1 => attackFromActive(owner, opp, owner.active.get.firstMove.get, additional)
            case 2 => attackFromActive(owner, opp, owner.active.get.secondMove.get, additional)
            case 3 => attackFromActive(owner, opp, owner.active.get.pass, additional)
            case _ => throw new Exception("Invalid move number")
        }
        case AttackFromBench(benchIndex : Int, moveNum : Int, additional : Seq[String])
                => (owner.bench(benchIndex).get, moveNum) match {
            case (bc : PokemonCard, 1) => attackFromBench(owner, opp, bc.firstMove.get, additional)
            case (bc : PokemonCard, 2) => attackFromBench(owner, opp, bc.secondMove.get, additional)
            case _ => throw new Exception("Invalid move number")
        }
    }

    def flipTurn(owner : Player, opp : Player) : Unit = {
        owner.isTurn = !owner.isTurn
        opp.isTurn = !opp.isTurn
    }

}
