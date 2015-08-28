package src.move

import src.board.move.CustomMoveInterpreter
import src.board.state.CustomStateGenerator
import src.board.drag.CustomDragInterpreter
import src.move.interceptor._
import src.player.Player

abstract class ActivePokemonPower(
  name : String,
  dragInterpreter : Option[CustomDragInterpreter] = None,
  moveInterpreter : Option[CustomMoveInterpreter] = None,
  clickInterceptor : Option[ClickInterceptor] = None,
  stateGenerator : Option[CustomStateGenerator] = None) extends PokemonPower(
    name,
    dragInterpreter,
    moveInterpreter,
    clickInterceptor,
    stateGenerator) {

  var isActive : Boolean = false

  override def perform(owner : Player, opp : Player) : Unit = {
    isActive = !isActive
    if (dragInterpreter.isDefined) {
      dragInterpreter.get.isActive = !dragInterpreter.get.isActive
    }
    if (stateGenerator.isDefined) {
      stateGenerator.get.isActive = !stateGenerator.get.isActive
    }
  }
  
}
