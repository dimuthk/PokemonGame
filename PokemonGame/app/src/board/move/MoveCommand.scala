package src.board.move

import src.board.intermediary.IntermediaryRequest
import src.card.Card
import src.card.energy.EnergyCard
import src.card.pokemon.PokemonCard
import src.board.drag._
import src.player.Player

sealed abstract class MoveCommand

case class AttackFromActive(moveNum : Int, additional : Seq[String]) extends MoveCommand

case class AttackFromBench(benchIndex : Int, moveNum : Int, additional : Seq[String]) extends MoveCommand