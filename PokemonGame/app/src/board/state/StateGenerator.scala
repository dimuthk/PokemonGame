package src.board.state

import src.card.Card
import src.card.pokemon.PokemonCard
import src.board.state.StateCommand
import src.player.Player
import play.api.libs.json._
import src.card.CardUI

/**
 * Generates a json container to send to clients, with the ui settings set according to the
 * side of the board, and whether or not it is a given player's turn.
 */
abstract class StateGenerator {

  def generateForPlayer1(p1 : Player, p2 : Player) : (JsObject, JsObject) = {
    orientStateForPlayer(p1, p1.isTurn)
    val p1Json = p1.toJson
    orientStateForPlayer(p2, p1.isTurn)
    val p2Json = p2.toJson
    return (p1Json, p2Json)
  }

  def generateForPlayer2(p1 : Player, p2 : Player) : (JsObject, JsObject) = {
    orientStateForPlayer(p2, p2.isTurn)
    val p2Json = p2.toJson
    orientStateForPlayer(p1, p2.isTurn)
    val p1Json = p1.toJson
    return (p2Json, p1Json)
  }

  def orientStateForPlayer(p : Player, isSouthTurn : Boolean) : Unit = {
  	val isSouth = isSouthTurn && p.isTurn
    p.active match {
      case Some(a) => a.setUiOrientation(generateUiFor(p, Active(a), isSouth))
      case None => ()
    }
    for (bc : PokemonCard <- p.bench.toList.flatten) {
      bc.setUiOrientation(generateUiFor(p, Bench(bc), isSouth))
    }
    for (hc : Card <- p.hand) {
      hc.setUiOrientation(generateUiFor(p, Hand(hc), isSouth))
    }
    for (pc : Card <- p.prizes.toList.flatten) {
      pc.setUiOrientation(generateUiFor(p, Prize(pc), isSouth))
    }
    if (p.deck.length > 0) {
      val dc = p.deck(0)
      dc.setUiOrientation(generateUiFor(p, Deck(dc), isSouth))
    }
  }

  def generateUiFor : (Player, StateCommand, Boolean) => Set[CardUI.Value]

}