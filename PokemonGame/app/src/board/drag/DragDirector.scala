package src.board.drag

import src.board.PlayerData
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
		val pData = PlayerData(owner, opp, isOwner)
		val dragCmd : DragCommand = contents(0) match {
			case "HAND_TO_ACTIVE" => HandToActive(contents(2).toInt)
			case "HAND_TO_BENCH" => HandToBench(contents(2).toInt, contents(3).toInt - 1)
			case "ACTIVE_TO_BENCH" => ActiveToBench(contents(2).toInt - 1)
			case "BENCH_TO_ACTIVE" => BenchToActive(contents(2).toInt - 1)
			case "BENCH_TO_BENCH" => BenchToBench(contents(2).toInt - 1, contents(3).toInt - 1)
		}
		val args = if (contents(0) == "HAND_TO_BENCH" || contents(0) == "BENCH_TO_BENCH")
			contents.drop(4) else contents.drop(3)

		interpreter.requestAdditional(pData, dragCmd, args) match {
			case Some(intermediary) => return processIntermediary(intermediary, owner, contents)
			case None => interpreter.handleDrag(pData, dragCmd, args)
		}
		return None
    }

    def processIntermediary(
    		intermediary : IntermediaryRequest,
    		owner : Player,
    		contents : Seq[String]) : Option[IntermediaryRequest] = {
    	var processed = intermediary
    	val flip : String = if (processed.p == owner) "" else "FLIP<>"
      	processed.serverTag = flip + "DRAG<>" + contents.mkString("<>") + "<>"
      	if (processed.additionalTag.isDefined) {
        	processed.serverTag = processed.serverTag + processed.additionalTag.get + "<>"
      	}
      	return Some(processed)
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