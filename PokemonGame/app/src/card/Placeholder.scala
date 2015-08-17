package src.card

import play.api.libs.json._
import src.json.Identifier

object Placeholder extends Card("Placeholder", "NO_IMG") {

	override def toJsonImpl() = Json.obj()

	override def getIdentifier() = Identifier.PLACEHOLDER

}