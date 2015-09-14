package src.board.drag

import src.board.InterpreterCommand

sealed abstract class DragCommand extends InterpreterCommand

case class HandToActive(hIndex : Int) extends DragCommand

case class HandToBench(hIndex : Int, bIndex : Int) extends DragCommand

case class ActiveToBench(bIndex : Int) extends DragCommand

case class BenchToActive(bIndex : Int) extends DragCommand

case class BenchToBench(bIndex : Int, bIndex2 : Int) extends DragCommand