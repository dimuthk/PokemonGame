package src.board.state

import src.card.pokemon.PokemonCard
import src.player.Player
import src.card.CardUI
import play.api.libs.json._

/**
 * A custom generator which can be implemented by outside resources. Custom
 * generations are hinged on a separate class, belonging to one of the players,
 * and thus have no concept of absolute board orientation. This class will
 * bind the absolute orientation to the relative one which can be interpreted
 * by the hinge class.
 */
abstract class CustomStateGenerator extends StateGenerator {

  var isActive : Boolean = false

  override def orientStateForPlayer(p : Player, isSouthTurn : Boolean) : Unit = {
    super.orientStateForPlayer(p, isSouthTurn)
    p.cardWithActivatedPower match {
      case Some(card) => card.setUiOrientation(uiForActivatedCard(p))
      case None => ()
    }
  }

  def uiForActivatedCard : (Player) => Set[CardUI.Value]

  def setCustomMoveFor : (Player, StateCommand, Boolean) => Option[JsArray]
    = (_, _, _) => None

  def customMoveForActivatedCard : (Player) => Option[JsArray] = (_) => None

}