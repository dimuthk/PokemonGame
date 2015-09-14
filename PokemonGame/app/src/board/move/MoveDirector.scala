package src.board.move

import src.board.Director
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

object MoveDirector extends Director[MoveCommand, MoveInterpreter]("MOVE") {

  def flippedHeads() : Boolean = true

  def buildCommand = (args) => args.head match {
    case "ATTACK_FROM_ACTIVE" => AttackFromActive(args(2).toInt)
    case "ATTACK_FROM_BENCH" => AttackFromBench(args(2).toInt - 1, args(3).toInt)
    case "ATTACK_FROM_DECK" => AttackFromDeck(args(2).toInt)
    case "ATTACK_FROM_HAND" => AttackFromHand(args(2).toInt, args(3).toInt)
    case "ATTACK_FROM_PRIZE" => AttackFromPrize(args(2).toInt, args(3).toInt)
  }

  def selectInterpreterArgs = (args) => args.head match {
    case "ATTACK_FROM_ACTIVE" => args.drop(3)
    case "ATTACK_FROM_DECK" => args.drop(3)
    case _ => args.drop(4)
  }

  def selectInterpreter(owner : Player, opp : Player) : MoveInterpreter = {
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
