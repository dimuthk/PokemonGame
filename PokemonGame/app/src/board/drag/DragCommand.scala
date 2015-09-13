package src.board.drag

sealed abstract class DragCommand

case class HandToActive(hIndex : Int) extends DragCommand

case class HandToBench(hIndex : Int, bIndex : Int) extends DragCommand

case class ActiveToBench(bIndex : Int) extends DragCommand

case class BenchToActive(bIndex : Int) extends DragCommand

case class BenchToBench(bIndex : Int, bIndex2 : Int) extends DragCommand