package src.board.move

import src.board.intermediary.IntermediaryRequest
import src.card.Card
import src.move.Move
import src.move.MoveBuilder._
import src.card.energy.EnergyCard
import src.card.pokemon.PokemonCard
import src.move.PokemonPower
import src.board.drag._
import src.player.Player

object DefaultMoveInterpreter extends MoveInterpreter {

  override def requestAdditional = (pData, moveCmd, args) => moveCmd match {
    case AttackFromActive(moveNum) => moveNum match {
      case 1 => checkAdditional(pData.owner, pData.opp, pData.owner.active.get.firstMove.get, args)
      case 2 => checkAdditional(pData.owner, pData.opp, pData.owner.active.get.secondMove.get, args)
      case 3 => None
      case _ => throw new Exception("Unexpected move num")
    }
    case AttackFromBench(bIndex, moveNum) => pData.owner.bench(bIndex).get.existingMoves(moveNum - 1) match {
      case power : PokemonPower => power.requestAdditional(pData.owner, pData.opp, args)
      case _ => throw new Exception("Non pokemon power move called from bench!") 
    }
  }
  

  override def handleCommand = (pData, moveCmd, args) => moveCmd match {
    case AttackFromActive(moveNum) => moveNum match {
      case 1 => useMove(pData.owner, pData.opp, pData.owner.active.get.firstMove.get, args)
      case 2 => useMove(pData.owner, pData.opp, pData.owner.active.get.secondMove.get, args)
      case 3 => useMove(pData.owner, pData.opp, pData.owner.active.get.pass, Nil)
      case _ => throw new Exception("Unexpected move num")
    }
    case AttackFromBench(bIndex, moveNum) => pData.owner.bench(bIndex).get.existingMoves(moveNum - 1) match {
      case power : PokemonPower => power.perform(pData.owner, pData.opp, args)
      case _ => throw new Exception("Non pokemon power move called from bench!") 
    }
    case _ => throw new Exception("Unsupported move command for default move handler")
  }

  def checkAdditional(owner : Player, opp : Player, move : Move, args : Seq[String]) : Option[IntermediaryRequest] = move match {
    case power : PokemonPower => power.requestAdditional(owner, opp, args)
    case _ => !owner.active.get.smokescreen || flippedHeads() match {
      case true => move.requestAdditional(owner, opp, args)
      case false => None
    }
  }

  def useMove(owner : Player, opp : Player, move : Move, args : Seq[String]) : Unit = move match {
    case power : PokemonPower => power.perform(owner, opp, args)
    case _ => {
      if (!owner.active.get.smokescreen || flippedHeads()) {
        move.perform(owner, opp, args)
      }
      owner.performedMove = true
    }
  }

}
