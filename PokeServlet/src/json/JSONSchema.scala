package json

/**
 * Standard values for json value mappings. All JSON object value names should be directly taken
 * from this list.
 */
object JSONSchema extends Enumeration {
  type JSONSchema = Value
  
  /*
   * GENERAL ATTRIBUTES
   */
  val IDENTIFIER = Value("iden")
  val DISPLAY_NAME = Value("dName")
  
  /*
   * POKEMON CARD ATTRIBUTES
   */
  val MAX_HP = Value("maxHp")
  val CURR_HP = Value("currHp")

}