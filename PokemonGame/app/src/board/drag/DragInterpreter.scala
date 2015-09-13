package src.board.drag

import src.board.PlayerData
import src.board.intermediary.IntermediaryRequest
import src.card.Card
import src.card.energy.EnergyCard
import src.card.pokemon.PokemonCard
import src.board.drag._
import src.player.Player

abstract class DragInterpreter {

    def requestAdditional : (PlayerData, DragCommand, Seq[String]) => Option[IntermediaryRequest]
        = (_, _, _) => None

    def handleDrag : (PlayerData, DragCommand, Seq[String]) => Unit

}
