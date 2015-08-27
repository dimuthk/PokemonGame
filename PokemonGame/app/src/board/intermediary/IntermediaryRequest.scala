package src.board.intermediary

import src.player.Player
import src.json.Jsonable
import src.json.Identifier
import src.card.Card
import actors.PlayerActor
import akka.actor._
import akka.actor.ActorRef
import src.json.Jsonable
import akka.event.{EventBus, LookupClassification}

import play.api.libs.json._

/**
 * A special intermediary request object which breaks the normal state flow between
 * client and server because the server is in need of additional information to
 * process the client's original request. The contract for these requests is that
 * the board state is NOT modified on a client request if an intermediary request
 * is determined to be required; the entire board state will then be processed upon
 * receiving the additional information. The request will generate some sort of
 * modal pop-up on the client's screen requesting the information.
 */
abstract class IntermediaryRequest(
	val requestTitle : String,
	val requestMsg : String,
	val p : Player) extends Jsonable {

	override def toJsonImpl() = Json.obj(
		Identifier.REQUEST_TITLE.toString -> requestTitle,
		Identifier.REQUEST_MSG.toString -> requestMsg)
}

/**
 * Generates a list of clickable cards to the user, where the user must click on a
 * preset number of cards and then submit the response.
 */
abstract class ClickableCardRequest(
	requestTitle : String,
	requestMsg : String,
	p : Player,
	val clickCount : Int,
	val cardList : Seq[Card]) extends IntermediaryRequest(requestTitle, requestMsg, p) {

	override def getIdentifier() = Identifier.CLICK_CARD_REQUEST

	override def toJsonImpl() = super.toJsonImpl() ++ Json.obj(
		Identifier.CLICK_COUNT.toString -> clickCount,
		Identifier.CARD_LIST.toString -> cardListToJsArray(cardList))

}