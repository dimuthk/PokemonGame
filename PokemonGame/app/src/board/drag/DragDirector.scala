package src.board.drag

import src.move.Move
import src.card.pokemon.PokemonCard
import src.player.Player
import src.board.intermediary.IntermediaryRequest
import play.api.Logger

object DragDirector {

	def handleDragCommand(
		owner : Player, opp : Player, contents : Seq[String]) : Option[IntermediaryRequest] = {
		val dragCmd : DragCommand = contents(0) match {
			case "HAND_TO_ACTIVE" => HandToActive(contents(1).toInt)
			case "HAND_TO_BENCH" => HandToBench(contents(1).toInt, contents(2).toInt - 1)
			case "ACTIVE_TO_BENCH" => ActiveToBench(contents(1).toInt - 1)
			case "BENCH_TO_ACTIVE" => BenchToActive(contents(1).toInt - 1)
			case "BENCH_TO_BENCH" => BenchToBench(contents(1).toInt - 1, contents(2).toInt - 1)
			case "INTERMEDIARY" => Intermediary(contents.tail)
			case _ => throw new Exception("Unrecognized drag request:  " + contents) 
		}

		val intermediaryReq = DefaultDragInterpreter.additionalRequest(owner, dragCmd)
		if (intermediaryReq == None) {
			val interpreter = selectDragInterpreter(owner, opp)
			interpreter.handleDrag(owner, dragCmd)
		} else {
			val flip : String = if (intermediaryReq.get.p == owner) "" else "FLIP<>"
      		intermediaryReq.get.serverTag = flip + "DRAG<>" + contents.mkString("<>") + "<>"
      		if (intermediaryReq.get.additionalTag.isDefined) {
      			intermediaryReq.get.serverTag = intermediaryReq.get.serverTag + intermediaryReq.get.additionalTag.get + "<>"
      		}
			Logger.debug("dragintermediaryReq: " + intermediaryReq)
		}
		return intermediaryReq
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