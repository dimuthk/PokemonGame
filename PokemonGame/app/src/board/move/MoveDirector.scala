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
    val moveCmd : MoveCommand = contents(0) match {
      case "ATTACK_FROM_ACTIVE" => AttackFromActive(contents(1).toInt, contents.drop(2))
      case "ATTACK_FROM_BENCH" => AttackFromBench(contents(1).toInt - 1, contents(2).toInt, contents.drop(3))
      //case "INTERMEDIARY" => Intermediary(contents.tail)
    }
    val intermediaryReq = DefaultMoveInterpreter.additionalRequest(owner, opp, moveCmd)
    if (intermediaryReq == None) {
      val interpreter = selectMoveInterpreter(owner, opp)
      interpreter.handleMove(owner, opp, moveCmd)
    }
    return intermediaryReq
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
