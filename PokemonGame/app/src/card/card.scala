package src.card

import play.api.libs.json._
import src.json.Jsonable
import src.json.Identifier

/**
 * Base class for all cards used in the game.
 */
abstract class Card(val displayName : String, val imgName : String, val deck : Deck.Value) extends Jsonable {

	var isFaceUp : Boolean = false

	var isClickable : Boolean = false

	var isDraggable : Boolean = false

	var isUsable : Boolean = false

	override def toJsonImpl() = Json.obj(
		Identifier.FACE_UP.toString -> JsBoolean(isFaceUp),
		Identifier.DRAGGABLE.toString -> JsBoolean(isDraggable),
		Identifier.CLICKABLE.toString -> JsBoolean(isClickable),
		Identifier.USABLE.toString -> JsBoolean(isUsable))
}