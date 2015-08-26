package src.board.intermediary

import src.card.Card
import actors.PlayerActor
import akka.actor._
import akka.actor.ActorRef
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
abstract class IntermediaryRequest(val requestTitle : String, val requestMsg : String)

/**
 * Generates a list of clickable cards to the user, where the user must click on a
 * preset number of cards and then submit the response.
 */
abstract class ClickableCardRequest(
	requestTitle : String,
	requestMsg : String,
	val clickCount : Int,
	val cardList : Seq[Card]) extends IntermediaryRequest(requestTitle, requestMsg)