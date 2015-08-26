package src.board.move

import src.board.move.MoveCommand
import src.board.intermediary.IntermediaryRequest
import src.card.Card
import src.move.Move
import src.move.PokemonPower
import src.card.energy.EnergyCard
import src.card.pokemon.PokemonCard
import src.card.pokemon.EvolutionStage
import src.board.drag._
import src.player.Player

object MoveDirector {

  def handleMoveCommand(owner : Player, opp : Player, contents : Seq[String]) : Option[IntermediaryRequest] = {
    val interpreter = selectMoveInterpreter()
    val moveCmd : MoveCommand = contents(0) match {
      case "ATTACK_FROM_ACTIVE" => AttackFromActive(contents(1).toInt)
      case "ATTACK_FROM_BENCH" => ActiveFromBench(contents(2).toInt, contents(1).toInt)
    }
    val intermediaryReq = interpreter.additionalRequest(owner, moveCmd)
    if (intermediaryReq == None) {
      interpreter.handleMove(owner, opp, moveCmd)
    }
    return intermediaryReq
  }

  def selectMoveInterpreter() : MoveInterpreter = {
    return DefaultMoveInterpreter
  }

}
