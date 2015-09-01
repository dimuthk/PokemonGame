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
    val interpreter = selectMoveInterpreter(owner, opp)
    val maybeIntermediaryReq  : Option[IntermediaryRequest] = contents(0) match {
      case "ATTACK_FROM_ACTIVE" => interpreter.attackFromActive(owner, opp, contents(1).toInt, contents.drop(2))
      case "ATTACK_FROM_BENCH" => interpreter.attackFromBench(owner, opp, contents(1).toInt - 1, contents(2).toInt, contents.drop(3))
    }
    if (maybeIntermediaryReq.isDefined) {
      val flip : String = if (maybeIntermediaryReq.get.p == owner) "" else "FLIP<>"
      maybeIntermediaryReq.get.serverTag = flip + "MOVE<>" + contents.mkString("<>") + "<>"
      if (maybeIntermediaryReq.get.additionalTag.isDefined) {
        maybeIntermediaryReq.get.serverTag = maybeIntermediaryReq.get.serverTag + maybeIntermediaryReq.get.additionalTag.get + "<>"
      }
    }
    return maybeIntermediaryReq
  }

  def flippedHeads() : Boolean = true

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
