package src.move

import src.board.state.CustomStateGenerator
import src.board.drag.CustomDragInterpreter
import src.move.interceptor._
import src.player.Player

abstract class PokemonPower(
  name : String,
  dragInterpreter : Option[CustomDragInterpreter] = None,
  moveInterceptor : Option[MoveInterceptor] = None,
  clickInterceptor : Option[ClickInterceptor] = None,
  stateGenerator : Option[CustomStateGenerator] = None) extends Move(
    name,
    0,
    Map(),
    dragInterpreter,
    moveInterceptor,
    clickInterceptor,
    stateGenerator) {

  override def perform(owner : Player, opp : Player) : Unit

}
