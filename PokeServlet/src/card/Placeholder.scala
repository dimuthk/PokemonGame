package card

import json.JSONIdentifier
import json.JSONSchema
import org.json.JSONObject

/**
 * @author dimuth
 */
object Placeholder extends Card("Placeholder", "IMG") {
  
  override def toJsonImpl() = new JSONObject()
      
  override def setJsonValues(json : JSONObject) = {}
  
  override def getIdentifier() = JSONIdentifier.PLACEHOLDER
  
}