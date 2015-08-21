package src.move

import src.card.pokemon.PokemonCard
import src.player.Player;

/**
 * A library of helper methods to help compose attacks; this will reduce
 * redundancy and help chain attacks together. All methods in this group
 * assume that preliminary battleground checks have already been made.
 */
object MoveBuilder {

  /**
   * The standard damage calculator for attacks. All attacks which do damage and
   * are not held to some additional stipulation should utilize this method.
   */
  def calculateDmg(owner : Player, opp : Player, dmg : Int) : Int = {
    var attacker = owner.active.get
    var defender = opp.active.get
    var modifiedDmg = dmg
    
    // Resistance / weakness modifier
    if (defender.weakness.exists { eType => eType == attacker.energyType }) {
      modifiedDmg *= 2
    }
    
    if (defender.resistance.exists { eType => eType == attacker.energyType }) {
      modifiedDmg -= 30
    }

    return math.max(modifiedDmg, 0)
  }
  
  /**
   * Standard attack which deals damage without any side effects.
   */
  def standardAttack(owner : Player, opp : Player, baseDmg : Int) : Unit = {
    var dmg = calculateDmg(owner, opp, baseDmg)
    opp.active.get.takeDamage(dmg)
  }
  
  /**
   * Does standard damage along with inflicting poison status.
   */
  def poisonAttack(owner : Player, opp : Player, baseDmg : Int) : Unit = {
    standardAttack(owner, opp, baseDmg)
    //opp.active.get.poisonStatus = PoisonStatus.POISONED
  }
  
}