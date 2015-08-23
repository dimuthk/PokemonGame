package src.card.condition

import src.player.Player

abstract class GeneralCondition(val name : String) {

  def modifyOffensive(owner : Player, opp : Player, expectedDmg : Int) : Int = expectedDmg

  def modifyDefensive(owner : Player, opp : Player, expectedDmg : Int) : Int = expectedDmg
}