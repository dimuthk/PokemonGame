package src.board.move

import src.board.Interpreter
import src.board.intermediary.IntermediaryRequest
import src.player.Player

abstract class MoveInterpreter extends Interpreter[MoveCommand] {

    def flipTurn(owner : Player, opp : Player) : Unit = {
        owner.isTurn = !owner.isTurn
        opp.isTurn = !opp.isTurn
    }

}
