package src.board.move

import src.board.intermediary.IntermediaryRequest
import src.card.Card
import src.move.Move
import src.card.energy.EnergyCard
import src.card.pokemon.PokemonCard
import src.card.pokemon.EvolutionStage
import src.board.drag._
import src.player.Player

abstract class MoveInterpreter {

    def additionalRequest(p : Player, command : MoveCommand) : Option[IntermediaryRequest]

    def attack(owner : Player, opp : Player, move : Move) : Unit

    def handleMove(owner : Player, opp : Player, cmd : MoveCommand) {
        cmd match {
            case AttackFromActive(moveNum : Int) => {
                if (moveNum == 1) {
                    attack(owner, opp, owner.active.get.firstMove.get)
                } else if (moveNum == 2) {
                    attack(owner, opp, owner.active.get.secondMove.get)
                } else {
                    throw new Exception("Invalid move number")
                }
            }
            case ActiveFromBench(benchIndex : Int, moveNum : Int) => {
                val bc : PokemonCard = owner.bench(benchIndex).get
                if (moveNum == 1) {
                    attack(owner, opp, bc.firstMove.get)
                } else if (moveNum == 2) {
                    attack(owner, opp, bc.secondMove.get)
                } else {
                    throw new Exception("Invalid move number")
                }
            }
        }
    }

}
