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

  override def orientStateForPlayer(p : Player, isSouthTurn : Boolean) : JsObject = {
    val isSouth = isSouthTurn && p.isTurn
    p.setUIOrientationForActive(uiForActive(p, isSouth))
    p.cardWithActivatedPower match {
      case Some(card) => card.setUiOrientation(uiForActivatedCard(p))
      case None => ()
    }
    return p.toJson
  }

  def uiForActive = (p, isSouth) => if (isSouth) uiForActiveSouth(p) else uiForActiveNorth(p)

  def uiForActiveNorth : (Player) => Set[CardUI.Value] = (p) => DefaultStateGenerator.uiForActive(p, false)

  def uiForActiveSouth : (Player) => Set[CardUI.Value]

  def uiForBench = (p, isSouth) => if (isSouth) uiForBenchSouth(p) else uiForBenchNorth(p)

  def uiForBenchNorth : (Player) => Set[CardUI.Value] = (p) => DefaultStateGenerator.uiForBench(p, false)

  def uiForBenchSouth : (Player) => Set[CardUI.Value]

  def uiForHand = (p, isSouth) => if (isSouth) uiForHandSouth(p) else uiForHandNorth(p)

  def uiForHandNorth : (Player) => Set[CardUI.Value] = (p) => DefaultStateGenerator.uiForHand(p, false)

  def uiForHandSouth : (Player) => Set[CardUI.Value]

  def uiForPrize = (p, isSouth) => if (isSouth) uiForPrizeSouth(p) else uiForPrizeNorth(p)

  def uiForPrizeNorth : (Player) => Set[CardUI.Value] = (p) => DefaultStateGenerator.uiForPrize(p, false)

  def uiForPrizeSouth : (Player) => Set[CardUI.Value]

  def uiForActivatedCard : (Player) => Set[CardUI.Value]

}