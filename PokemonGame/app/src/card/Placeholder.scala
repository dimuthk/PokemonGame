package src.card

import play.api.libs.json._
import src.json.Identifier
import src.json.Jsonable

object Placeholder extends Jsonable {

	override def toJsonImpl() = Json.obj()

	override def getIdentifier() = Identifier.PLACEHOLDER

}
