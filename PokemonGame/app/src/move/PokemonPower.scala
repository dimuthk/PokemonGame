package src.move

import src.board.state.CustomStateGenerator
import src.board.drag.CustomDragInterpreter
import src.board.move.CustomMoveInterpreter
import src.player.Player

abstract class PokemonPower(
  name : String,
  dragInterpreter : Option[CustomDragInterpreter] = None,
  moveInerpreter : Option[CustomMoveInterpreter] = None,
  stateGenerator : Option[CustomStateGenerator] = None) extends Move(
    name,
    0,
    Map(),
    dragInterpreter,
    moveInerpreter,
    stateGenerator)