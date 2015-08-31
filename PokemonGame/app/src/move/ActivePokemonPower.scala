package src.move

import src.board.intermediary.IntermediaryRequest
import src.card.pokemon.PokemonCard
import src.board.state.CustomStateGenerator
import src.board.drag.CustomDragInterpreter
import src.player.Player

import play.api.Logger

abstract class ActivePokemonPower(
  name : String,
  dragInterpreter : Option[CustomDragInterpreter] = None,
  stateGenerator : Option[CustomStateGenerator] = None) extends PokemonPower(
    name,
    dragInterpreter,
    stateGenerator) {

  private var _isActive : Boolean = false

  def isActive = _isActive

  def togglePower() : Option[IntermediaryRequest] = {
    _isActive = !_isActive
    if (dragInterpreter.isDefined) {
      dragInterpreter.get.isActive = !dragInterpreter.get.isActive
    }
    if (stateGenerator.isDefined) {
      stateGenerator.get.isActive = !stateGenerator.get.isActive
    }
    return None
  }

  override def update = (owner, _, pc, _, _)
      => (pc.statusCondition, owner.cardWithActivatedPower) match {
    // Inflicted with a status condition
    case (Some(_), _) => Status.DISABLED
    // A different card of yours has an activated power
    case (_, Some(card)) if card != pc => Status.DISABLED
    case _ => if (isActive) Status.ACTIVATED else Status.ACTIVATABLE
  }
  
}
