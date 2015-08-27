package src.board.drag

import src.card.Card
import src.card.energy.EnergyCard
import src.card.pokemon.PokemonCard
import src.card.pokemon.EvolutionStage
import src.board.drag._
import src.player.Player

abstract class CustomDragInterpreter extends DragInterpreter {

    var isActive : Boolean = false

}
