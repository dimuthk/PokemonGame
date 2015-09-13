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
    val isOwner = contents(1) == "1"
    val maybeIntermediaryReq  : Option[IntermediaryRequest] = contents(0) match {
      case "ATTACK_FROM_ACTIVE" => interpreter.attackFromActive(owner, opp, isOwner, contents(2).toInt, contents.drop(3))
      case "ATTACK_FROM_BENCH" => interpreter.attackFromBench(owner, opp, isOwner, contents(2).toInt - 1, contents(3).toInt, contents.drop(4))
      case "ATTACK_FROM_DECK" => interpreter.attackFromDeck(owner, opp, isOwner, contents(2).toInt - 1, contents.drop(3))
      case "ATTACK_FROM_HAND" => interpreter.attackFromHand(owner, opp, isOwner, contents(2).toInt - 1, contents(3).toInt, contents.drop(4))
      case "ATTACK_FROM_PRIZE" => interpreter.attackFromPrize(owner, opp, isOwner, contents(2).toInt - 1, contents(3).toInt, contents.drop(4))
    }

    /*
    contents(0) match {
      case "ATTACK_FROM_ACTIVE" => {
        val moveNum = contents(2).toInt
        val args = contents.drop(3)
        interpreter.attackFromActiveAdditional(owner, opp, isOwner, moveNum, args) match {
          case Some(intermediary) => return processIntermediary(intermediary, owner, contents)
          case None => interpreter.attackFromActive(owner, opp, isOwner, moveNum, args)
        }
      }
      case "ATTACK_FROM_BENCH" => {
        val bIndex = contents(2).toInt - 1
        val moveNum = contents(3).toInt
        val args = contents.drop(4)
        interpreter.attackFromBenchAdditional(owner, opp, isOwner, bIndex, moveNum, args) match {
          case Some(intermediary) => return processIntermediary(intermediary, owner, contents)
          case None => interpreter.attackFromBench(owner, opp, isOwner, moveNum, args)
        }
      }
      case "HAND_TO_BENCH" => {
        val hIndex = contents(2).toInt
        val bIndex = contents(3).toInt - 1
        val args = contents.drop(4)
        interpreter.handToBenchAdditional(owner, opp, isOwner, hIndex, bIndex, args) match {
          case Some(intermediary) => return processIntermediary(intermediary, owner, contents)
          case None => interpreter.handToBench(owner, opp, isOwner, hIndex, bIndex, args)
        }
      }
      case "ACTIVE_TO_BENCH" => {
        val bIndex = contents(2).toInt - 1
        val args = contents.drop(3)
        interpreter.activeToBenchAdditional(owner, opp, isOwner, bIndex, args) match {
          case Some(intermediary) => return processIntermediary(intermediary, owner, contents)
          case None => interpreter.activeToBench(owner, opp, isOwner, bIndex, args)
        }
      }
      case "BENCH_TO_ACTIVE" => {
        val bIndex = contents(2).toInt - 1
        val args = contents.drop(3)
        interpreter.benchToActiveAdditional(owner, opp, isOwner, bIndex, args) match {
          case Some(intermediary) => return processIntermediary(intermediary, owner, contents)
          case None => interpreter.benchToActive(owner, opp, isOwner, bIndex, args)
        }
      }
      case "BENCH_TO_BENCH" => {
        val bIndex1 = contents(2).toInt - 1
        val bIndex2 = contents(3).toInt - 1
        val args = contents.drop(4)
        interpreter.benchToBenchAdditional(owner, opp, isOwner, bIndex1, bIndex2, args) match {
          case Some(intermediary) => return processIntermediary(intermediary, owner, contents)
          case None => interpreter.benchToBench(owner, opp, isOwner, bIndex1, bIndex2, args)
        }
      }
    }
    return None*/



   
    if (maybeIntermediaryReq.isDefined) {
      val flip : String = if (maybeIntermediaryReq.get.p == owner) "" else "FLIP<>"
      maybeIntermediaryReq.get.serverTag = flip + "MOVE<>" + contents.mkString("<>") + "<>"
      if (maybeIntermediaryReq.get.additionalTag.isDefined) {
        maybeIntermediaryReq.get.serverTag = maybeIntermediaryReq.get.serverTag + maybeIntermediaryReq.get.additionalTag.get + "<>"
      }
    }
    return maybeIntermediaryReq
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
