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

  def uiForActive = (p, isSouth) => (p.isTurn, isSouth) match {
    case (true, true) => Set(FACE_UP, CLICKABLE, DISPLAYABLE, DRAGGABLE, USABLE)
    case _ => Set(FACE_UP, CLICKABLE, DISPLAYABLE)
  }

  def uiForBench = (p, isSouth) => (p.isTurn, isSouth) match {
    case (true, true) => Set(FACE_UP, CLICKABLE, DISPLAYABLE, DRAGGABLE, USABLE)
    case _ => Set(FACE_UP, CLICKABLE, DISPLAYABLE)
  }

  def uiForHand = (p, isSouth) => (p.isTurn, isSouth) match {
    case (true, true) => Set(FACE_UP, DRAGGABLE, CLICKABLE)
    case (false, true) => Set(FACE_UP)
    case _ => Set()
  }

  def uiForPrize = (p, isSouth) => Set()

  def uiForDeck = (p, isSouth) => Set()

}