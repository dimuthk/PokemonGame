package src.json

import src.card.Card
import src.card.Placeholder
import play.api.libs.json._

import scala.reflect.ClassTag

trait Jsonable {

  def toJsonImpl() : JsObject

  def getIdentifier() : Identifier.Value

  final def toJson() : JsObject = toJsonImpl() +
      (Identifier.IDENTIFIER.toString() -> Json.toJson(getIdentifier))

  def cardListToJsArray(list : Seq[Card]) : JsArray = {
    return list.foldRight(new JsArray())((c, curr) => curr.prepend(c.toJson))
  }

  def optionCardListToJsArray[T <: Card : ClassTag](list : Seq[Option[T]]) : JsArray = {
    return list.foldRight(new JsArray())((c, curr) => curr.prepend(optionCardToJson(c)))
  }

  def optionCardToJson[T <: Card : ClassTag](oc : Option[T]) : JsObject = oc match {
    case Some(c) => c.toJson
    case None => Placeholder.toJson
  }

}
