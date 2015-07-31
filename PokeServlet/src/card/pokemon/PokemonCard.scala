package card.pokemon

import org.json.JSONObject
import card.Card
import json.JSONIdentifier
import json.JSONSchema

abstract class PokemonCard(
    displayName : String,
    imgName : String,
    val maxHp : Integer) extends Card(displayName, imgName) {

  var currHp : Integer = maxHp
  
  override def setJsonValues(json : JSONObject) {
    currHp = json.get(JSONSchema.CURR_HP.toString()) match {
      case hp : Integer => hp
      case _ => throw new ClassCastException()
    }
  }

  override def toJsonImpl() = new JSONObject()
      .put(JSONSchema.DISPLAY_NAME.toString(), displayName)
      .put(JSONSchema.MAX_HP.toString(), maxHp)
      .put(JSONSchema.CURR_HP.toString(), currHp)

}