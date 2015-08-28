package src.board.state

import src.card.Card
import src.card.pokemon.PokemonCard
import src.player.Player
import play.api.libs.json._

/**
 * Generates a json container to send to clients, with the ui settings set according to the
 * side of the board, and whether or not it is a given player's turn.
 */
object DefaultStateGenerator extends StateGenerator {

  def generateForPlayer1(p1 : Player, p2: Player) : (JsObject, JsObject) = {
  	return (orientStateForPlayer(p1, p1.isTurn), orientStateForPlayer(p2, p1.isTurn))
  }

  def generateForPlayer2(p1 : Player, p2 : Player) : (JsObject, JsObject) = {
  	return (orientStateForPlayer(p2, p2.isTurn), orientStateForPlayer(p1, p2.isTurn))
  }

  /**
   * Update any UI specifications for this player. The specification hinges on whether
   * this is the south or north side of the board, and whether it's this player's turn.
   */
  private def orientStateForPlayer(p : Player, yourTurn : Boolean) : JsObject = {
    val yourCards = p.isTurn == yourTurn
    // You should only ever be able to click on the other player's cards.
    for (oc : Option[PokemonCard] <- (p.bench ++ List(p.active))) {
      if (oc.isDefined) {
        // active and bench cards are always face up
        oc.get.isFaceUp = true
        oc.get.isDraggable = yourCards && yourTurn
        oc.get.isClickable = true
        oc.get.isDisplayable = true
        oc.get.isUsable = yourCards && yourTurn
      }
    }
    for (c : Card <- p.hand) {
      c.isFaceUp = yourCards
      c.isDraggable = yourCards && yourTurn
      c.isClickable = yourCards && yourTurn
      c.isDisplayable = yourCards && yourTurn
      c.isUsable = false
    }
    return p.toJson
  }

}