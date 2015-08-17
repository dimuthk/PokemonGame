package src.card.pokemon

import play.api.libs.json._
import src.card.Card
import src.json.Identifier

abstract class PokemonCard(
	displayName : String,
	imgName : String,
	val maxHp : Int) extends Card(displayName, imgName) {

	var currHp : Int = maxHp

	override def toJsonImpl() = Json.obj(
		Identifier.DISPLAY_NAME.toString -> displayName,
		Identifier.MAX_HP.toString -> maxHp,
		Identifier.CURR_HP.toString -> currHp,
		Identifier.IMG_NAME.toString -> imgName)
	
}