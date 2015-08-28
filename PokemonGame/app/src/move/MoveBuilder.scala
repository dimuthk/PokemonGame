package src.move

import src.card.pokemon.PokemonCard
import src.card.condition.GeneralCondition
import src.card.condition.PoisonStatus
import src.card.condition.StatusCondition
import src.card.energy.EnergyType
import src.player.Player

import scala.util.Random

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

  def standardAttackPlusExtra(
      owner : Player,
      opp : Player,
      baseDmg : Int,
      eType : EnergyType.Value,
      initialReq : Int) : Unit = {
    var newDmg = baseDmg
    val eCount = owner.active.get.getTotalEnergy(Some(eType))
    if (eCount - initialReq > 1) {
      newDmg = baseDmg + 20
    } else if (eCount - initialReq > 0) {
      newDmg = baseDmg + 10
    }
    standardAttack(owner, opp, newDmg)
  }

  /**
   * Does standard damage along with inflicting poison status.
   */
  def poisonAttack(owner : Player, opp : Player, baseDmg : Int) : Unit = {
    standardAttack(owner, opp, baseDmg)
    opp.active.get.poisonStatus = Some(PoisonStatus.POISONED)
  }

  def energyDiscardAttack(
        owner : Player,
        opp : Player,
        baseDmg : Int,
        eType : EnergyType.Value,
        cnt : Int = 1) : Unit = {
    standardAttack(owner, opp, baseDmg)
    val discardedCards = owner.active.get.discardEnergy(eType, cnt)
    owner.garbage = owner.garbage ++ discardedCards   
  }

  def paralyzeChanceAttack(owner : Player, opp : Player, baseDmg : Int) : Unit = {
    standardAttack(owner, opp, baseDmg)
    if (flippedHeads()) {
      opp.active.get.statusCondition = Some(StatusCondition.PARALYZED)
      opp.notify(opp.active.get.displayName + " is paralyzed!")
    }
  }

  def activateMoveInterpreterChance(owner : Player, m : Move, name : String) {
    if (flippedHeads()) {
      m.moveInterpreter.get.isActive = true
      owner.active.get.generalCondition = Some(name)
      owner.notify(owner.active.get.displayName + " successfully performed " + name + "!")
    }
  }

  def flippedHeads() = Random.nextBoolean()
  
}