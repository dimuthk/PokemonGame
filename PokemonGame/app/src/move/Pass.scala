package src.move

import src.card.pokemon.PokemonCard
import play.api.libs.json._
import src.board.move.MoveCommand
import src.board.intermediary.IntermediaryRequest
import src.board.drag.CustomDragInterpreter
import src.board.move.CustomMoveInterpreter
import src.board.state.CustomStateGenerator
import src.card.energy.EnergyCard
import src.card.energy.EnergyType
import src.json.Identifier
import src.json.Jsonable
import src.player.Player

import play.api.Logger

class Pass extends Move("Pass", 0) {

  def perform = (owner, opp) => ()


  override def update(owner : Player, opp : Player, pc : PokemonCard, turnSwapped : Boolean, isActive : Boolean) : Unit =
      // Status is enabled iff it's your turn, this card is in the active slot,
      // the card has enough energy to perform the move, and you don't currently
      // have an activated power on the board.
      status = (
        owner.isTurn,
        isActive,
        hasEnoughEnergy(owner, pc.energyCards),
        owner.cardWithActivatedPower == None) match {
        case (true, true, true, true) =>  Status.ENABLED
        case _ => Status.DISABLED
      }

}