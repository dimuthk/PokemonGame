package src.board.intermediary

import src.player.Player
import src.json.Jsonable
import src.json.Identifier
import src.card.Card
import src.card.pokemon.PokemonCard
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
 * Add the flip call to the beginning if necessary.
 * Standard: FLIP?<>MOVE/DRAG<>INTERMEDIARY<>...
 */
abstract class IntermediaryRequest(
	val requestTitle : String,
	val requestMsg : String,
	var serverTag : String,
	val p : Player) extends Jsonable {

	override def toJsonImpl() = Json.obj(
		Identifier.REQUEST_TITLE.toString -> requestTitle,
		Identifier.SERVER_TAG.toString -> serverTag,
		Identifier.REQUEST_MSG.toString -> requestMsg)
}

object IntermediaryRequest {

	def getRealIndexFor(flatIndex : Int, bench : Seq[Option[PokemonCard]]) : Int = {
        var matcher : Int = -1
        for (realIndex : Int <- 0 until 5) {
            if (bench(realIndex).isDefined) {
                matcher = matcher + 1
            }
            if (matcher == flatIndex) {
                return realIndex
            }
        }
        throw new Exception("Did not get matching index for flattened index")
    }

}

/**
 * Generates a list of clickable cards to the user, where the user must click on a
 * preset number of cards and then submit the response. This message should NOT be exitable.
 */
abstract class ClickableCardRequest(
	requestTitle : String,
	requestMsg : String,
	serverTag : String,
	p : Player,
	val clickCount : Int,
	val cardList : Seq[Card]) extends IntermediaryRequest(requestTitle, requestMsg, serverTag, p) {

	override def getIdentifier() = Identifier.CLICK_CARD_REQUEST

	override def toJsonImpl() = super.toJsonImpl() ++ Json.obj(
		Identifier.CLICK_COUNT.toString -> clickCount,
		Identifier.CARD_LIST.toString -> cardListToJsArray(cardList))

}

/**
 * Same as ClickableCardRequest, but only certain cards are clickable.
 * TODO: This request needs to be exitable, since it's possible you have no matches.
 */
abstract class SpecificClickableCardRequest(
	requestTitle : String,
	requestMsg : String,
	serverTag : String,
	p : Player,
	val clickCount : Int,
	val cardList : Seq[Card]) extends IntermediaryRequest(requestTitle, requestMsg, serverTag, p) {

	override def getIdentifier() = Identifier.SPECIFIC_CLICK_CARD_REQUEST

	override def toJsonImpl() = super.toJsonImpl() ++ Json.obj(
		Identifier.MATCHER.toString -> selectersToJsArray(cardList),
		Identifier.CLICK_COUNT.toString -> clickCount,
		Identifier.CARD_LIST.toString -> cardListToJsArray(cardList))

	def isSelectable : Card => Boolean

	def selectersToJsArray(list : Seq[Card]) : JsArray = {
    	return list.foldRight(new JsArray())((c, curr) => curr.prepend(JsBoolean(isSelectable(c))))
  	}


}
