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

  def doSomething : Unit = {}

    def f : (Player, Player) => Unit => Int = (owner, opp) => doSomething => 3

    def attackFromActive = (owner, opp, _, moveNum, args) => moveNum match {
      case 1 => useMove(owner, opp, owner.active.get.firstMove.get, args)
      case 2 => useMove(owner, opp, owner.active.get.secondMove.get, args)
      case 3 => useMove(owner, opp, owner.active.get.pass, Nil)
      case _ => throw new Exception("Unexpected move num")
    }

    def useMove(owner : Player, opp : Player, move : Move, args : Seq[String]) : Option[IntermediaryRequest] = move match {
      case power : PokemonPower => power.perform(owner, opp, args)
      case _ => {
        val intermediary = (!owner.active.get.smokescreen || flippedHeads()) match {
          case true => move.perform(owner, opp, args)
          case false => None
        }
        if (intermediary.isEmpty) {
          flipTurn(owner, opp)
        }
        return intermediary
      }
    }

    def attackFromBench = (owner, opp, _, benchIndex, moveNum, args) => owner.bench(benchIndex).get.existingMoves(moveNum - 1) match {
      case power : PokemonPower => power.perform(owner, opp, args)
      case _ => throw new Exception("Non pokemon power move called from bench!")
    }

    def attackFromPrize = (_, _, _, _, _, _) => throw new Exception("Prize attack not supported by default")

    def attackFromHand = (_, _, _, _, _, _) => throw new Exception("Hand attack not supported by default")

    def attackFromDeck = (_, _, _, _, _) => throw new Exception("Deck attack not supported by default")

}
