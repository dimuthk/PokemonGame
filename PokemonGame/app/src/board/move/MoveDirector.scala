package src.board.move

import src.board.PlayerData
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
    val isOwner = contents(1) == "1"
    val pData = PlayerData(owner, opp, isOwner)
    val moveCmd : MoveCommand = contents(0) match {
      case "ATTACK_FROM_ACTIVE" => AttackFromActive(contents(2).toInt)
      case "ATTACK_FROM_BENCH" => AttackFromBench(contents(2).toInt - 1, contents(3).toInt)
      case "ATTACK_FROM_DECK" => AttackFromDeck(contents(2).toInt)
      case "ATTACK_FROM_HAND" => AttackFromHand(contents(2).toInt, contents(3).toInt)
      case "ATTACK_FROM_PRIZE" => AttackFromPrize(contents(2).toInt, contents(3).toInt)
    }

    val args = if (contents(0) == "ATTACK_FROM_ACTIVE" || contents(0) == "ATTACK_FROM_DECK")
      contents.drop(3) else contents.drop(4)

    interpreter.requestAdditional(pData, moveCmd, args) match {
      case Some(intermediary) => return processIntermediary(intermediary, owner, contents)
      case None => interpreter.handleMove(pData, moveCmd, args)
    }
    return None
  }

  def processIntermediary(
        intermediary : IntermediaryRequest,
        owner : Player,
        contents : Seq[String]) : Option[IntermediaryRequest] = {
      var processed = intermediary
      val flip : String = if (processed.p == owner) "" else "FLIP<>"
        processed.serverTag = flip + "MOVE<>" + contents.mkString("<>") + "<>"
        if (processed.additionalTag.isDefined) {
          processed.serverTag = processed.serverTag + processed.additionalTag.get + "<>"
        }
        return Some(processed)
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
