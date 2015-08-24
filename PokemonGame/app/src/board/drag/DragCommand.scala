package src.board.drag

sealed abstract class DragCommand

case class HandToActive(handIndex : Int) extends DragCommand

case class HandToBench(handIndex : Int, benchIndex : Int) extends DragCommand

case class ActiveToBench(benchIndex : Int) extends DragCommand

case class BenchToActive(benchIndex : Int) extends DragCommand

case class BenchToBench(benchIndex1 : Int, benchIndex2 : Int) extends DragCommand