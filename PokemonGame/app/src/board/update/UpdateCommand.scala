package src.board.update

import src.board.InterpreterCommand

sealed abstract class UpdateCommand extends InterpreterCommand

case class Update() extends UpdateCommand