package src.json

import src.card.Card
import src.card.Placeholder
import src.move.Move
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

  def identifiersToJsArray(list : Set[Identifier.Value]) : JsArray = {
    return list.foldRight(new JsArray())((i, curr) => curr.prepend(JsString(i.toString)))
  }

  def optionCardListToJsArray[T <: Card : ClassTag](list : Seq[Option[T]]) : JsArray = {
    return list.foldRight(new JsArray())((c, curr) => curr.prepend(optionCardToJson(c)))
  }

  def optionMoveListToJsArray(list : Seq[Option[Move]]) : JsArray = {
    return list.foldRight(new JsArray())((m, curr) => curr.prepend(optionMoveToJson(m)))
  }

  def optionMoveToJson(om : Option[Move]) : JsObject = om match {
    case Some(m) => m.toJson
    case None => Placeholder.toJson
  }

  def optionCardToJson[T <: Card : ClassTag](oc : Option[T]) : JsObject = oc match {
    case Some(c) => c.toJson
    case None => Placeholder.toJson
  }

}
