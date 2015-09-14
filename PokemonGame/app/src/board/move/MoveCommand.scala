package src.board.move

import src.board.InterpreterCommand
import src.board.intermediary.IntermediaryRequest
import src.card.Card
import src.card.energy.EnergyCard
import src.card.pokemon.PokemonCard
import src.board.drag._
import src.player.Player

sealed abstract class MoveCommand extends InterpreterCommand

case class AttackFromActive(moveNum : Int) extends MoveCommand

case class AttackFromBench(bIndex : Int, moveNum : Int) extends MoveCommand

case class AttackFromHand(hIndex : Int, moveNum : Int) extends MoveCommand

case class AttackFromDeck(moveNum : Int) extends MoveCommand

case class AttackFromPrize(pIndex : Int, moveNum : Int) extends MoveCommand