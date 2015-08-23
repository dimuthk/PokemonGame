package src.json

// next id: 56
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
  val NOTIFICATION = Value(44)
  val IS_TURN = Value(51)

  /*
   * CARD ATTRIBUTES
   */
   val FACE_UP = Value(52)
   val DRAGGABLE = Value(53)
   val CLICKABLE = Value(54)
   val USABLE = Value(55)


  /*
   * ENERGY CARD ATTRIBUTES
   */
  val ENERGY_TYPE = Value(25)
  val FIRE_ENERGY = Value(17)
  val WATER_ENERGY = Value(18)
  val GRASS_ENERGY = Value(19)
  val THUNDER_ENERGY = Value(20)
  val FIGHTING_ENERGY = Value(21)
  val PSYCHIC_ENERGY = Value(22)
  val DOUBLE_COLORLESS_ENERGY = Value(23)
   
  /*
   * POKEMON CARD ATTRIBUTES
   */
  val MAX_HP = Value(4)
  val CURR_HP = Value(5)
  val ENERGY_CARDS = Value(24)
  val MOVES = Value(37)

  /*
   * MOVE ATTRIBUTES
   */
  val MOVE = Value(36)
  val MOVE_NAME = Value(26)
  val MOVE_ENABLED = Value(29)
  val MOVE_STATUS = Value(35)

  /**
   * STATUS ATTRIBUTES
   */
  val POISON_STATUS = Value(33)
  val STATUS_CONDITION = Value(45)
  val GENERAL_CONDITION = Value(46)
   
  /*
   * POKEMON
   */
  val MACHOP = Value(6)
  val RATTATA = Value(15)
  val BULBASAUR = Value(31)
  val IVYSAUR = Value(32)
  val VENUSAUR = Value(34) 
  val CHARMANDER = Value(38)
  val CHARMELEON = Value(39)
  val CHARIZARD = Value(40)
  val SQUIRTLE = Value(41)
  val WARTORTLE = Value(42)
  val BLASTOISE = Value(43)
  val CATERPIE = Value(48)
  val METAPOD = Value(49)
  val BUTTERFREE = Value(50)
     
}
