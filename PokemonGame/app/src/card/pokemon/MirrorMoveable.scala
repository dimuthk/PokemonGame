package src.card.pokemon

import play.api.libs.json._
import src.card.Placeholder
import src.card.Card
import src.player.Player
import src.card.Deck
import src.card.condition.GeneralCondition
import src.card.condition.PoisonStatus
import src.card.condition.StatusCondition
import src.card.energy.EnergyCard
import src.card.energy.EnergyType
import src.json.Identifier
import src.move.Move

trait MirrorMoveable extends PokemonCard {

  override def updateCardOnTurnSwap(owner : Player, opp : Player, isActive : Boolean) : Unit = {
    super.updateCardOnTurnSwap(owner, opp, isActive)
    if (lastAttack != -1) {
      generalCondition = Some("Mirror Move: " + lastAttack + "damage")
    }
  }

}