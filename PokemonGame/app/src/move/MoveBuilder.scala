package src.move

import src.board.intermediary.IntermediaryRequest
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
  def standardAttack(owner : Player, opp : Player, baseDmg : Int) : Option[IntermediaryRequest] = {
    var dmg = opp.active.get.calculateDmg(owner.active.get, baseDmg)
    opp.active.get.takeDamage(dmg)
    return None
  }

  def standardAttackPlusExtra(
      owner : Player,
      opp : Player,
      baseDmg : Int,
      eType : EnergyType.Value,
      initialReq : Int) : Option[IntermediaryRequest] = {
    var newDmg = baseDmg
    val eCount = owner.active.get.getTotalEnergy(Some(eType))
    if (eCount - initialReq > 1) {
      newDmg = baseDmg + 20
    } else if (eCount - initialReq > 0) {
      newDmg = baseDmg + 10
    }
    return standardAttack(owner, opp, newDmg)
  }

  def multipleHitAttack(owner : Player, opp : Player, baseDmg : Int, flips : Int) : Option[IntermediaryRequest] = {
    for (_ <- 0 until flips) {
      standardAttack(owner, opp, baseDmg)
    }
    return None
  }

  def energyDrainAttack(owner : Player, opp : Player, baseDmg : Int) : Option[IntermediaryRequest] = {
    owner.active.get.heal(opp.active.get.calculateDmg(owner.active.get, baseDmg))
    return standardAttack(owner, opp, baseDmg)
  }

  def extraDamageOrHurtSelf(owner : Player, opp : Player, baseDmg : Int, extraDmg : Int) : Option[IntermediaryRequest] = {
    standardAttack(owner, opp, baseDmg)
    flippedHeads() match {
      case true => standardAttack(owner, opp, extraDmg)
      case false => owner.active.get.takeDamage(extraDmg)
    }
    return None
  }

  def selfDamageChanceAttack(owner : Player, opp : Player, baseDmg : Int, selfDmg : Int) : Option[IntermediaryRequest] = {
    if (flippedHeads()) {
      owner.active.get.takeDamage(selfDmg)
      owner.notify(owner.active.get.displayName + " hurt itself while attacking!")
    }
    return standardAttack(owner, opp, baseDmg)
  }

  def sleepAttack(owner : Player, opp : Player, baseDmg : Int = 0) : Option[IntermediaryRequest]
    = setStatusConditionAttack(owner, opp, StatusCondition.ASLEEP, baseDmg, false)

  def sleepAttackChance(owner : Player, opp : Player, baseDmg : Int = 0) : Option[IntermediaryRequest]
    = setStatusConditionAttack(owner, opp, StatusCondition.ASLEEP, baseDmg, true)

  def confuseAttack(owner : Player, opp : Player, baseDmg : Int = 0) : Option[IntermediaryRequest]
    = setStatusConditionAttack(owner, opp, StatusCondition.CONFUSED, baseDmg, false)

  def confuseAttackChance(owner : Player, opp : Player, baseDmg : Int = 0) : Option[IntermediaryRequest]
    = setStatusConditionAttack(owner, opp, StatusCondition.CONFUSED, baseDmg, true)

  def paralyzeAttackChance(owner : Player, opp : Player, baseDmg : Int = 0) : Option[IntermediaryRequest]
    = setStatusConditionAttack(owner, opp, StatusCondition.PARALYZED, baseDmg, true)

  def poisonAttack(owner : Player, opp : Player, baseDmg : Int = 0) : Option[IntermediaryRequest]
    = setPoisonStatusAttack(owner, opp, PoisonStatus.POISONED, baseDmg, false)

  def poisonAttackChance(owner : Player, opp : Player, baseDmg : Int = 0) : Option[IntermediaryRequest]
    = setPoisonStatusAttack(owner, opp, PoisonStatus.POISONED, baseDmg, true)

  private def setStatusConditionAttack(
    owner : Player,
    opp : Player,
    statusCondition : StatusCondition.Value,
    baseDmg : Int,
    shouldFlip : Boolean) : Option[IntermediaryRequest] = {
    if (!shouldFlip || flippedHeads()) {
      owner.active.get.statusCondition = Some(statusCondition)
    }
    return standardAttack(owner, opp, baseDmg)
  }

  private def setPoisonStatusAttack(
    owner : Player,
    opp : Player,
    poisonStatus : PoisonStatus.Value,
    baseDmg : Int,
    shouldFlip : Boolean) : Option[IntermediaryRequest] = {
    if (!shouldFlip || flippedHeads()) {
      owner.active.get.poisonStatus = Some(poisonStatus)
    }
    return standardAttack(owner, opp, baseDmg)
  }

  def energyDiscardAttack(
        owner : Player,
        opp : Player,
        baseDmg : Int,
        eType : EnergyType.Value,
        cnt : Int = 1) : Option[IntermediaryRequest] = {
    val discardedCards = owner.active.get.discardEnergy(eType, cnt)
    owner.garbage = owner.garbage ++ discardedCards
    return standardAttack(owner, opp, baseDmg)
  }

  def flippedHeads() = true //Random.nextBoolean()
  
}