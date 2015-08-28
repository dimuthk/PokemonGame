package src.move

import src.board.state.CustomStateGenerator
import src.board.drag.CustomDragInterpreter
import src.board.move.CustomMoveInterpreter
import src.move.interceptor._
import src.player.Player

abstract class PokemonPower(
  name : String,
  dragInterpreter : Option[CustomDragInterpreter] = None,
  moveInterpreter : Option[CustomMoveInterpreter] = None,
  clickInterceptor : Option[ClickInterceptor] = None,
  stateGenerator : Option[CustomStateGenerator] = None) extends Move(
    name,
    0,
    Map(),
    dragInterpreter,
    moveInterpreter,
    clickInterceptor,
    stateGenerator) {

  override def perform(owner : Player, opp : Player) : Unit

}
