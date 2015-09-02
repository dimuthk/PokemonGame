package src.card

import play.api.libs.json._
import src.json.Jsonable
import src.json.Identifier

/**
 * Base class for all cards used in the game.
 */
abstract class Card(val displayName : String, val imgName : String, val deck : Deck.Value) extends Jsonable {

	var isClickable : Boolean = false

	var isDraggable : Boolean = false

	var isFaceUp : Boolean = false

	var isDisplayable : Boolean = false

	var isUsable : Boolean = false

	def setUiOrientation(uiSet : Set[CardUI.Value]) {
		isFaceUp = uiSet.contains(CardUI.FACE_UP)
		isClickable = uiSet.contains(CardUI.CLICKABLE)
		isDraggable = uiSet.contains(CardUI.DRAGGABLE)
		isUsable = uiSet.contains(CardUI.USABLE)
		isDisplayable = uiSet.contains(CardUI.DISPLAYABLE)
	}

	def customMoveJson(customMoveArray : Option[JsArray]) : JsObject = toJsonImpl() ++ Json.obj(
		Identifier.MOVES.toString -> (customMoveArray match {
      		case Some(moveArray) => moveArray
      		case None => throw new Exception("There is no default impl for base card")
    	}))

	override def toJsonImpl() = Json.obj(
		Identifier.FACE_UP.toString -> JsBoolean(isFaceUp),
		Identifier.DRAGGABLE.toString -> JsBoolean(isDraggable),
		Identifier.CLICKABLE.toString -> JsBoolean(isClickable),
		Identifier.USABLE.toString -> JsBoolean(isUsable),
		Identifier.DISPLAYABLE.toString -> JsBoolean(isDisplayable))
}

object CardUI extends Enumeration {
	type CardUI = Value
	val FACE_UP, CLICKABLE, DRAGGABLE, DISPLAYABLE, USABLE = Value
}