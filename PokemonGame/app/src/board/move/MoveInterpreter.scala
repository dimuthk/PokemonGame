package src.board.move

import src.board.PlayerData
import src.board.intermediary.IntermediaryRequest
import src.card.Card
import src.move.Move
import src.card.energy.EnergyCard
import src.card.pokemon.PokemonCard
import src.board.drag._
import src.player.Player

abstract class MoveInterpreter {

    def requestAdditional : (PlayerData, MoveCommand, Seq[String]) => Option[IntermediaryRequest]
        = (_, _, _) => None

    def handleMove : (PlayerData, MoveCommand, Seq[String]) => Unit

    def flipTurn(owner : Player, opp : Player) : Unit = {
        owner.isTurn = !owner.isTurn
        opp.isTurn = !opp.isTurn
    }

}
