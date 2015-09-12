package src.board.move

import src.board.intermediary.IntermediaryRequest
import src.card.Card
import src.move.Move
import src.card.energy.EnergyCard
import src.card.pokemon.PokemonCard
import src.board.drag._
import src.player.Player

abstract class CustomMoveInterpreter extends MoveInterpreter {

	var isActive : Boolean = false

	/*private var intermediary : Option[IntermediaryRequest] = None

	def requestIntermediary(i : IntermediaryRequest) : Unit = intermediary = Some(i)

	def attackFromActiveImpl : (Player, Player, Boolean, Int, Seq[String]) => Unit



	def attackFromActive = (owner, opp, isOwner, moveNum, args) = {
		attackFromActiveImpl(owner, opp, isOwner, moveNum, args)
		return intermediary
	}*/

}
