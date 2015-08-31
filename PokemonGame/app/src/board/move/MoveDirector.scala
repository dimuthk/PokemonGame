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
    val interpreter = selectInterpreter()
    val maybeIntermediaryReq  : Option[IntermediaryRequest] = contents(0) match {
      case "ATTACK_FROM_ACTIVE" => contents(1).toInt match {
        case 1 => interpreter.attackFromActive(owner, opp, owner.active.get.firstMove.get, contents.drop(2))
        case 2 => interpreter.attackFromActive(owner, opp, owner.active.get.secondMove.get, contents.drop(2))
        case 3 => interpreter.attackFromActive(owner, opp, owner.active.get.pass, Nil)
        case _ => throw new Exception("Invalid move number")
      }
      case "ATTACK_FROM_BENCH" => (contents(1).toInt - 1, contents(2).toInt) match {
        case (benchIndex, 1) => interpreter.attackFromBench(owner, opp, owner.bench(benchIndex).get.firstMove.get, contents.drop(3))
        case (benchIndex, 2) => interpreter.attackFromBench(owner, opp, owner.bench(benchIndex).get.secondMove.get, contents.drop(3))
        case _ => throw new Exception("Invalid move number")
      }
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

  def selectInterpreter() : MoveInterpreter = DefaultMoveInterpreter


}
