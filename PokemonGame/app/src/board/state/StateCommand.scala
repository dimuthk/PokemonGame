package src.board.state

import src.player.Player
import src.board.InterpreterCommand

sealed abstract class StateCommand extends InterpreterCommand

abstract class ActiveOrBench extends StateCommand

case class Active() extends ActiveOrBench

case class Bench(i : Integer) extends ActiveOrBench

case class Hand(i : Integer) extends StateCommand

case class Prize(i : Integer) extends StateCommand

case class Deck() extends StateCommand