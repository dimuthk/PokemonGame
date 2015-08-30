package src.board.move

import src.board.move.MoveCommand
import src.board.intermediary.IntermediaryRequest
import src.card.Card
import src.move.Move
import src.move.PokemonPower
import src.card.energy.EnergyCard
import src.card.pokemon.PokemonCard
import src.board.drag._
import src.player.Player
import play.api.Logger

object MoveDirector {

  def handleMoveCommand(owner : Player, opp : Player, contents : Seq[String]) : Option[IntermediaryRequest] = {
    val originalCmd = contents.head
    val moveCmd : MoveCommand = originalCmd match {
      case "ATTACK_FROM_ACTIVE" => AttackFromActive(contents(1).toInt, contents.drop(2))
      case "ATTACK_FROM_BENCH" => AttackFromBench(contents(1).toInt - 1, contents(2).toInt, contents.drop(3))
    }
    val maybeIntermediaryReq = DefaultMoveInterpreter.additionalRequest(owner, opp, moveCmd)
    maybeIntermediaryReq match {
      case Some(intermediaryReq) => {
        intermediaryReq.serverTag = "MOVE<>" + contents.mkString("<>") + "<>"
      }
      case None => {
        val interpreter = selectMoveInterpreter(owner, opp)
        interpreter.handleMove(owner, opp, moveCmd)
      }
    }
    return maybeIntermediaryReq
  }

  def selectMoveInterpreter(owner : Player, opp : Player) : MoveInterpreter = {
    for (pc : PokemonCard <- (owner.existingActiveAndBenchCards ++ opp.existingActiveAndBenchCards)) {
      for (m : Move <- pc.existingMoves) {
        if (m.moveInterpreter.isDefined && m.moveInterpreter.get.isActive) {
          Logger.debug("got special interpreter")
          return m.moveInterpreter.get
        }
      }
    }
    return DefaultMoveInterpreter
  }

}
