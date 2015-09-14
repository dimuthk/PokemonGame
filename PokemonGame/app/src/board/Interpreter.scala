package src.board

import src.board.intermediary.IntermediaryRequest

import scala.reflect.ClassTag

abstract class Interpreter[T <: InterpreterCommand : ClassTag] {

	def requestAdditional : (PlayerData, T, Seq[String])
		=> Option[IntermediaryRequest] = (_, _, _) => None

    def handleCommand : (PlayerData, T, Seq[String]) => Unit

}