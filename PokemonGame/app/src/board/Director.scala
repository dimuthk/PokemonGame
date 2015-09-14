package src.board

import src.board.Interpreter
import src.board.InterpreterCommand
import src.board.intermediary.IntermediaryRequest
import src.player.Player

import scala.reflect.ClassTag

abstract class Director[
  C <: InterpreterCommand : ClassTag,
  I <: Interpreter[C] : ClassTag](val tag : String) {

  def handleCommand(owner : Player, opp : Player, args : Seq[String]) : Option[IntermediaryRequest] = {
    val isOwner = args(1) == "1"
    val interpreter = selectInterpreter(owner, opp)
    val pData = PlayerData(owner, opp, isOwner)
    val cmd = buildCommand(args)
    val interpreterArgs = selectInterpreterArgs(args)
    return interpreter.requestAdditional(pData, cmd, interpreterArgs) match {
      case Some(intermediary) => processIntermediary(intermediary, owner, args)
      case None => {
        interpreter.handleCommand(pData, cmd, interpreterArgs)
        None
      }
    }
  }

  def selectInterpreterArgs : Seq[String] => Seq[String]

  def selectInterpreter(owner : Player, opp : Player) : I

  def buildCommand : (Seq[String]) => C

  def processIntermediary(
        intermediary : IntermediaryRequest,
        owner : Player,
        contents : Seq[String]) : Option[IntermediaryRequest] = {
      var processed = intermediary
      val flip : String = if (processed.p == owner) "" else "FLIP<>"
        processed.serverTag = flip + tag + "<>" + contents.mkString("<>") + "<>"
        if (processed.additionalTag.isDefined) {
          processed.serverTag = processed.serverTag + processed.additionalTag.get + "<>"
        }
        return Some(processed)
  }

}