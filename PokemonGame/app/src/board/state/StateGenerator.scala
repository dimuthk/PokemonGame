package src.board.state

import src.player.Player
import play.api.libs.json._

/**
 * Generates a json container to send to clients, with the ui settings set according to the
 * side of the board, and whether or not it is a given player's turn.
 */
abstract class StateGenerator {

  def generateForPlayer1(p1 : Player, p2: Player) : (JsObject, JsObject)

  def generateForPlayer2(p1 : Player, p2: Player) : (JsObject, JsObject)

}