package src.move

import src.card.pokemon.PokemonCard
import src.board.state.CustomStateGenerator
import src.board.drag.CustomDragInterpreter
import src.player.Player

abstract class PassivePokemonPower(
  name : String,
  dragInterpreter : Option[CustomDragInterpreter] = None,
  stateGenerator : Option[CustomStateGenerator] = None) extends PokemonPower(
    name,
    dragInterpreter,
    stateGenerator) {

  def perform = (owner, opp, args) => throw new Exception("Tried to use passive pokemon power")

  override def update = (_, _, pc, _, _) => status = (pc.statusCondition) match {
    case Some(_) => Status.DISABLED
    case _ => Status.PASSIVE
  }

}
