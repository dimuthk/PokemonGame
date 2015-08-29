package src.move

import src.card.pokemon.PokemonCard
import src.board.move.CustomMoveInterpreter
import src.board.state.CustomStateGenerator
import src.board.drag.CustomDragInterpreter
import src.player.Player

import play.api.Logger

abstract class ActivePokemonPower(
  name : String,
  dragInterpreter : Option[CustomDragInterpreter] = None,
  moveInterpreter : Option[CustomMoveInterpreter] = None,
  stateGenerator : Option[CustomStateGenerator] = None) extends PokemonPower(
    name,
    dragInterpreter,
    moveInterpreter,
    stateGenerator) {

  private var _isActive : Boolean = false

  def isActive = _isActive

  def perform = (owner, opp) => {
    _isActive = !_isActive
    if (dragInterpreter.isDefined) {
      dragInterpreter.get.isActive = !dragInterpreter.get.isActive
    }
    if (stateGenerator.isDefined) {
      stateGenerator.get.isActive = !stateGenerator.get.isActive
    }
  }

  override def update(
    owner : Player,
    opp : Player,
    pc : PokemonCard,
    turnSwapped : Boolean,
    isActiveCard : Boolean) : Unit = {
    status = (pc.statusCondition, owner.cardWithActivatedPower) match {
      // Inflicted with a status condition
      case (Some(_), _) => Status.DISABLED
      // A different card of yours has an activated power
      case (_, Some(card)) if card != pc => Status.DISABLED
      case _ => if (isActive) Status.ACTIVATED else Status.ACTIVATABLE 
  }}
  
}
