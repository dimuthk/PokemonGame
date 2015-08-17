package src.json

import play.api.libs.json._

trait Jsonable {

  def toJsonImpl() : JsObject

  def getIdentifier() : Identifier.Value

  final def toJson() : JsObject = toJsonImpl() +
      (Identifier.IDENTIFIER.toString() -> Json.toJson(getIdentifier))

}
