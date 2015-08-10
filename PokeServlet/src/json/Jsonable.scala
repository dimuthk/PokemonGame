package json

import org.json.JSONObject

/**
 * All objects which are explicitly pass to/from server must implement the jsonable interface.
 */
trait Jsonable {
  
  /**
   * Custom JSON parcel constructed for each class.
   */
  def toJsonImpl() : JSONObject
  
  /**
   * Assigns appropriate state values from corresponding json parcel.
   */
  def setJsonValues(json : JSONObject) : Unit
  
  
  def getIdentifier() : JSONIdentifier.Value
  
  /**
   * Converts the jsonable object into a json parcel.
   */
  final def toJson() : JSONObject = toJsonImpl().put(JSONSchema.IDENTIFIER.toString(), getIdentifier())

}