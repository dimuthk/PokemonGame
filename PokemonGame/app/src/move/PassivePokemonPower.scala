package src.move

import src.board.move.CustomMoveInterpreter
import src.board.state.CustomStateGenerator
import src.board.drag.CustomDragInterpreter
import src.player.Player

abstract class PassivePokemonPower(
  name : String,
  dragInterpreter : Option[CustomDragInterpreter] = None,
  moveInterpreter : Option[CustomMoveInterpreter] = None,
  stateGenerator : Option[CustomStateGenerator] = None) extends PokemonPower(
    name,
    dragInterpreter,
    moveInterpreter,
    stateGenerator) {

  override def perform(owner : Player, opp : Player) : Unit = ()
  
}
