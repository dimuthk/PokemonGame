package src.board.state

import src.card.Card
import src.card.pokemon.PokemonCard
import src.card.energy.EnergyCard
import src.player.Player
import play.api.libs.json._
import src.card.CardUI
import src.card.CardUI._

/**
 * Generates a json container to send to clients, with the ui settings set according to the
 * side of the board, and whether or not it is a given player's turn.
 */
object DefaultStateGenerator extends StateGenerator {

  def generateForPlayer1 = (p1, p2) => (orientStateForPlayer(p1, p1.isTurn), orientStateForPlayer(p2, p1.isTurn))

  def generateForPlayer2 = (p1, p2) => (orientStateForPlayer(p2, p2.isTurn), orientStateForPlayer(p1, p2.isTurn))

  /**
   * Update any UI specifications for this player. The specification hinges on whether
   * this is the south or north side of the board, and whether it's this player's turn.
   */
  private def orientStateForPlayer(p : Player, yourTurn : Boolean) : JsObject = p.prizesToAward match {
    case 0 => {
      val yourCards = p.isTurn == yourTurn
      // You should only ever be able to click on the other player's cards.
      for (pc : PokemonCard <- p.existingActiveAndBenchCards) {
        // active and bench cards are always face up
        p.setUIOrientationForActiveAndBench(
          Set(FACE_UP, CLICKABLE, DISPLAYABLE)
            ++ (if (yourCards && yourTurn) Set(DRAGGABLE, USABLE) else Nil))
      }
      for (c : Card <- p.hand) {
        p.setUiOrientationForHand(
          (if (yourCards) Set(FACE_UP) else Set())
            ++ (if (yourCards && yourTurn) Set(DRAGGABLE, CLICKABLE) else Nil))
      }
      for (c : Card <- p.prizes.toList.flatten) {
        p.setUiOrientationForPrize(Set())
      }
      return p.toJson
    }
    case _ => {
      val yourCards = p.isTurn == yourTurn
      for (pc : PokemonCard <- p.existingActiveAndBenchCards) {
        p.setUIOrientationForActiveAndBench(Set())
      }
      return p.toJson
    }
  }

}