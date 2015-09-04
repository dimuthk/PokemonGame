package src.board.drag

import src.move.Move
import src.card.pokemon.PokemonCard
import src.player.Player
import src.board.intermediary.IntermediaryRequest
import play.api.Logger

object DragDirector {

	def handleDragCommand(
		owner : Player, opp : Player, contents : Seq[String]) : Option[IntermediaryRequest] = {
		val isOwner = contents(1) == "1"
		val interpreter = selectDragInterpreter(owner, opp)

		val maybeIntermediaryReq : Option[IntermediaryRequest] = contents(0) match {
			case "HAND_TO_ACTIVE" => interpreter.handToActive(owner, opp, isOwner, contents(2).toInt, contents.drop(3))
			case "HAND_TO_BENCH" => interpreter.handToBench(owner, opp, isOwner, contents(2).toInt, contents(3).toInt -1, contents.drop(4))
			case "ACTIVE_TO_BENCH" => interpreter.activeToBench(owner, opp, isOwner, contents(2).toInt - 1, contents.drop(3))
			case "BENCH_TO_ACTIVE" => interpreter.benchToActive(owner, opp, isOwner, contents(2).toInt - 1, contents.drop(3))
			case "BENCH_TO_BENCH" => interpreter.benchToBench(owner, opp, isOwner, contents(2).toInt - 1, contents(3).toInt - 1, contents.drop(4))
			case _ => throw new Exception("Unrecognized drag request: " + contents)
		}

		if (maybeIntermediaryReq.isDefined) {
      		val flip : String = if (maybeIntermediaryReq.get.p == owner) "" else "FLIP<>"
      		maybeIntermediaryReq.get.serverTag = flip + "DRAG<>" + contents.mkString("<>") + "<>"
      		if (maybeIntermediaryReq.get.additionalTag.isDefined) {
        		maybeIntermediaryReq.get.serverTag = maybeIntermediaryReq.get.serverTag + maybeIntermediaryReq.get.additionalTag.get + "<>"
      		}
      	}
      	return maybeIntermediaryReq
    }

	/**
	 * For now, only allow custom interpreters to intercept drags made by their owner.
	 */
	def selectDragInterpreter(owner : Player, opp : Player) : DragInterpreter = {
		for (pc : PokemonCard <- owner.existingActiveAndBenchCards) {
			for (m : Move <- pc.existingMoves) {
				if (m.dragInterpreter.isDefined && m.dragInterpreter.get.isActive) {
					Logger.debug("Got a custom dragger")
					return m.dragInterpreter.get
				}
			}
		}
		return DefaultDragInterpreter
	}

}