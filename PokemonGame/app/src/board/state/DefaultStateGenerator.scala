package src.board.state

import src.board.state._
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

  def generateUiFor = (p, cmd, isSouth) => cmd match {
    case _ : ActiveOrBench => (p.isTurn, isSouth) match {
      case (true, true) => Set(FACE_UP, CLICKABLE, DISPLAYABLE, DRAGGABLE, USABLE)
      case _ => Set(FACE_UP, CLICKABLE, DISPLAYABLE)
    }
    case Hand(_) => (p.isTurn, isSouth) match {
      case (true, true) => Set(FACE_UP, DRAGGABLE, CLICKABLE)
      case (false, true) => Set(FACE_UP)
      case _ => Set()
    }
    case _ => Set()
  }

}