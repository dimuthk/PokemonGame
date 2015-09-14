package src.board.drag

import src.board.Director
import src.board.PlayerData
import src.move.Move
import src.card.pokemon.PokemonCard
import src.player.Player
import src.board.intermediary.IntermediaryRequest
import play.api.Logger

object DragDirector extends Director[DragCommand, DragInterpreter]("DRAG") {

  	def buildCommand = (args) => args.head match {
    	case "HAND_TO_ACTIVE" => HandToActive(args(2).toInt)
		case "HAND_TO_BENCH" => HandToBench(args(2).toInt, args(3).toInt - 1)
		case "ACTIVE_TO_BENCH" => ActiveToBench(args(2).toInt - 1)
		case "BENCH_TO_ACTIVE" => BenchToActive(args(2).toInt - 1)
		case "BENCH_TO_BENCH" => BenchToBench(args(2).toInt - 1, args(3).toInt - 1)
  	}

  	def selectInterpreterArgs = (args) => args.head match {
    	case "HAND_TO_BENCH" => args.drop(4)
    	case "BENCH_TO_BENCH" => args.drop(4)
    	case _ => args.drop(3)
  	}

	/**
	 * For now, only allow custom interpreters to intercept drags made by their owner.
	 */
	def selectInterpreter(owner : Player, opp : Player) : DragInterpreter = {
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