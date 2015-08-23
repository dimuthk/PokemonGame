package src.move

import src.player.Player

abstract class PokemonPower(
  name : String,
  isActivatable : Boolean) extends Move(
    name,
    0,
    Map(),
    isActivatable) {

  var activated = false

  override def canUse(owner : Player, opp : Player) : Boolean = {
    return true
  }

  override def perform(owner : Player, opp : Player) : Unit = {
    if (isActivatable) {
      activated = true
    }
  }

  def handleMove(owner : Player, opp : Player, moveName : String, itemMap : Map[String, Int]) : Unit = ()

}

