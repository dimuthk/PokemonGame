package json

/**
 * Type identifier for all jsonable objects. We use these to quickly associate between a jsonable parcel
 * and its corresponding class.
 */
object JSONIdentifier extends Enumeration {
  type JSONIdentifier = Value
  val MACHOP = Value(1)
  val PLAYER = Value(2)
  val PLACEHOLDER = Value(3)
}