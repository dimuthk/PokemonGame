package src.board.move

import src.board.intermediary.IntermediaryRequest
import src.card.Card
import src.move.Move
import src.move.PokemonPower
import src.card.energy.EnergyCard
import src.card.pokemon.PokemonCard
import src.card.pokemon.EvolutionStage
import src.board.drag._
import src.player.Player

object DefaultMoveInterpreter extends MoveInterpreter {

    override def additionalRequest(p : Player, command : MoveCommand) : Option[IntermediaryRequest] = None

    def attack(owner : Player, opp : Player, move : Move) : Unit = {
      move.perform(owner, opp)
      move match {
        case power : PokemonPower => ()
        case _ => flipTurn(owner, opp)
      }
    }

    def flipTurn(owner : Player, opp : Player) : Unit = {
        owner.isTurn = !owner.isTurn
        opp.isTurn = !opp.isTurn
    }

}
