package src.move

import src.card.energy.EnergyCard
import src.card.energy.EnergyType
import src.player.Player

abstract class Move(
	val name : String,
	val totalEnergyReq : Int,
	val specialEnergyReq : Map[EnergyType.Value, Int],
	private val action : (Player, Player) => Unit) {

	/**
	 * Flag marking that this move is disabled for reasons outside of energy requirements.
	 */
	var disabled = false

	def hasEnoughEnergy(energyCards : Seq[EnergyCard]) : Boolean = {
		val total = energyCards.map { card => card.energyCount }.sum
		if (total < totalEnergyReq) {
			return false
		}

		for ((energyType, reqCnt) <- specialEnergyReq) {
			val specialCntTotal = energyCards
				.filter(_.eType == energyType)
				.map { card => card.energyCount }.sum
			if (specialCntTotal < reqCnt) {
				return false
			}
    	}
    	return true
  }
  
  def canUse(owner : Player, opp : Player) : Boolean = {
    return hasEnoughEnergy(owner.active.get.energyCards)
  }
  
  /**
   * Used to perform the attack. This will make preliminary checks such as
   * that the board is setup properly, etc.
   */
  def perform(owner : Player, opp : Player) : Unit = {
    if (opp.active.isEmpty || owner.active.isEmpty) {
      // throw exception, this should never happen
      return;
    }
    action(owner, opp)
  }

}