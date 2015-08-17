package src.json

// next id: 
object Identifier extends Enumeration {

  type Identifier = Value

  implicit def identToString(ident : Identifier.Value) : String = ident.toString()

  /*
   * GENERAL ATTRIBUTES
   */
  val IDENTIFIER = Value(1)
  val DISPLAY_NAME = Value(2)
  val PLACEHOLDER = Value(7)
  val IMG_NAME = Value(14)
  val BOARD = Value(16)

  /*
   * PLAYER ATTRIBUTES
   */
  val PLAYER = Value(3)
  val ACTIVE = Value(8)
  val BENCH = Value(9)
  val HAND = Value(10)
  val DECK = Value(11)
  val GARBAGE = Value(12)
  val PRIZES = Value(13)
   
  /*
   * POKEMON CARD ATTRIBUTES
   */
  val MAX_HP = Value(4)
  val CURR_HP = Value(5)
   
  /*
   * POKEMON
   */
  val MACHOP = Value(6)
  val RATTATA = Value(15)
     
}
