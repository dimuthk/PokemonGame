package src.board.state

import src.player.Player
import play.api.libs.json._
import src.card.CardUI

/**
 * Generates a json container to send to clients, with the ui settings set according to the
 * side of the board, and whether or not it is a given player's turn.
 */
abstract class StateGenerator {

  def generateForPlayer1(p1 : Player, p2 : Player) : (JsObject, JsObject) =
  	(orientStateForPlayer(p1, p1.isTurn), orientStateForPlayer(p2, p1.isTurn))

  def generateForPlayer2(p1 : Player, p2 : Player) : (JsObject, JsObject) =
  	(orientStateForPlayer(p2, p2.isTurn), orientStateForPlayer(p1, p2.isTurn))

  def orientStateForPlayer(p : Player, isSouthTurn : Boolean) : JsObject = {
  	val isSouth = isSouthTurn && p.isTurn
  	p.setUIOrientationForActive(uiForActive(p, isSouth))
  	return p.toJson
  }

  def uiForActive : (Player, Boolean) => Set[CardUI.Value]

  def uiForBench : (Player, Boolean) => Set[CardUI.Value]

  def uiForHand : (Player, Boolean) => Set[CardUI.Value]

  def uiForPrize : (Player, Boolean) => Set[CardUI.Value]

}