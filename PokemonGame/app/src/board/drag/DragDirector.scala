package src.board.drag

import src.move.Move
import src.card.pokemon.PokemonCard
import src.player.Player
import src.board.intermediary.IntermediaryRequest

object DragDirector {

	def handleDragCommand(
		owner : Player, opp : Player, contents : Seq[String]) : Option[IntermediaryRequest] = {
		val interpreter = selectDragInterpreter(owner, opp)
		val dragCmd : DragCommand = contents(0) match {
			case "HAND_TO_ACTIVE" => HandToActive(contents(1).toInt)
			case "HAND_TO_BENCH" => HandToBench(contents(1).toInt, contents(2).toInt - 1)
			case "ACTIVE_TO_BENCH" => ActiveToBench(contents(1).toInt - 1)
			case "BENCH_TO_ACTIVE" => BenchToActive(contents(1).toInt - 1)
			case "BENCH_TO_BENCH" => BenchToBench(contents(1).toInt - 1, contents(2).toInt - 1)
			case "INTERMEDIARY" => Intermediary(contents.tail)
			case _ => throw new Exception("Unrecognized drag request:  " + contents) 
		}
		val intermediaryReq = interpreter.additionalRequest(owner, dragCmd)
		if (intermediaryReq == None) {
			interpreter.handleDrag(owner, dragCmd)
		}
		return intermediaryReq
	}

	/**
	 * For now, only allow custom interpreters to intercept drags made by their owner.
	 */
	def selectDragInterpreter(owner : Player, opp : Player) : DragInterpreter = {
		for (pc : PokemonCard <- owner.getExistingActiveAndBenchCards()) {
			for (m : Move <- pc.getExistingMoves()) {
				if (m.dragInterpreter.isDefined && m.dragInterpreter.get.isActive) {
					return m.dragInterpreter.get
				}
			}
		}
		return DefaultDragInterpreter
	}

}