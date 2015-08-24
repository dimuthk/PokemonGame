package src.move

import src.board.state.CustomStateGenerator
import src.board.drag.CustomDragInterpreter
import src.move.interceptor._
import src.player.Player

abstract class PokemonPower(
  name : String,
  val isActivatable : Boolean,
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

  var activated = false

  override def perform(owner : Player, opp : Player) : Unit = {
    activated = true
  }

  def handleMove(owner : Player, opp : Player, moveName : String, itemMap : Map[String, Int]) : Unit = ()

}
