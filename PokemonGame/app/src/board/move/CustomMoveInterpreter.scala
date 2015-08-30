package src.board.move

import src.move.ActivePokemonPower
import src.board.intermediary.IntermediaryRequest
import src.card.Card
import src.move.Move
import src.card.energy.EnergyCard
import src.card.pokemon.PokemonCard
import src.board.drag._
import src.player.Player

abstract class CustomMoveInterpreter extends MoveInterpreter {

    var isActive : Boolean = false

}
