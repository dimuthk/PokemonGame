package src.card.condition

import src.player.Player

class WithdrawCondition extends GeneralCondition("Withdraw") {
	override def modifyDefensive(owner : Player, opp : Player, dmg : Int) : Int = {
	  owner.notify(opp.active.get.displayName + "'s withdraw prevented the attack!")
      return 0
	}
}