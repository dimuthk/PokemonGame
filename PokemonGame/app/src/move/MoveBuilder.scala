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

  def roundUp(num : Int) : Int = num % 10 match {
    case 0 => num
    case x => num + (10 - x)
  }
  
  /**
   * Standard attack which deals damage without any side effects.
   */
  def standardAttack(owner : Player, opp : Player, baseDmg : Int) : Unit = {
    var dmg = opp.active.get.calculateDmg(owner.active.get, baseDmg)
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

  def multipleHitAttack(owner : Player, opp : Player, baseDmg : Int, flips : Int) : Unit = {
    for (_ <- 0 until flips) {
      standardAttack(owner, opp, baseDmg)
    }
  }

  def poisonChanceAttack(owner : Player, opp : Player, baseDmg : Int) : Unit = {
    standardAttack(owner, opp, baseDmg)
    if (flippedHeads()) {
      opp.active.get.poisonStatus = Some(PoisonStatus.POISONED)
      opp.notify(opp.active.get.displayName + " is poisoned!")
    }
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

  def flippedHeads() = true //Random.nextBoolean()
  
}