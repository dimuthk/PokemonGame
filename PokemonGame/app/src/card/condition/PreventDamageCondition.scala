package src.card.condition

import src.player.Player

/**
 * Condition which prevents all damage done to the pokemon. Other effects
 * (such as getting paralyzed) can still happen.
 */
class PreventDamageCondition(name : String) extends GeneralCondition(name) {
	override def modifyDefensive(owner : Player, opp : Player, dmg : Int) : Int = {
	  owner.notify(opp.active.get.displayName + "'s " + name + " prevented damage!")
      return 0
	}
}