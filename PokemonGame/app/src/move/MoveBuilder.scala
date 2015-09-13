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
  def standardAttack(owner : Player, opp : Player, dmg : Int) : Unit
    = opp.active.get.takeDamage(owner.active, dmg)

  def ignoreTypesAttack(owner : Player, opp : Player, dmg : Int) : Unit
    = opp.active.get.takeDamage(owner.active, dmg, useModifiers = false)

  def standardAttackPlusExtra(
      owner : Player,
      opp : Player,
      baseDmg : Int,
      eType : EnergyType.Value,
      initialReq : Int) {
    var newDmg = baseDmg
    val eCount = owner.active.get.getTotalEnergy(Some(eType))
    if (eCount - initialReq > 1) {
      newDmg = baseDmg + 20
    } else if (eCount - initialReq > 0) {
      newDmg = baseDmg + 10
    }
    standardAttack(owner, opp, newDmg)
  }

  def multipleHitAttack(owner : Player, opp : Player, baseDmg : Int, flips : Int) {
    for (_ <- 0 until flips) {
      standardAttack(owner, opp, baseDmg)
    }
  }

  def healthDrainAttack(owner : Player, opp : Player, dmg : Int) {
    val dealtDmg = opp.active.get.takeDamage(owner.active, dmg)
    owner.active.get.heal(dealtDmg)
  }

  def attackAndHurtSelf(owner : Player, opp : Player, baseDmg : Int, selfDmg : Int) {
    standardAttack(owner, opp, baseDmg)
    owner.active.get.takeDamage(None, selfDmg)
  }

  def extraDamageOrHurtSelf(
      owner : Player, opp : Player, baseDmg : Int, extraDmg : Int) : Unit = flippedHeads() match {
    case true => standardAttack(owner, opp, baseDmg + extraDmg)
    case false => {
      owner.active.get.takeDamage(None, extraDmg)
      standardAttack(owner, opp, baseDmg)
    }
  }

  def selfDamageChanceAttack(owner : Player, opp : Player, baseDmg : Int, selfDmg : Int) {
    if (flippedHeads()) {
      owner.active.get.takeDamage(None, selfDmg)
      owner.notify(owner.active.get.displayName + " hurt itself while attacking!")
    }
    standardAttack(owner, opp, baseDmg)
  }

  def sleepAttack(owner : Player, opp : Player, baseDmg : Int = 0)
    = setStatusConditionAttack(owner, opp, StatusCondition.ASLEEP, baseDmg, false)

  def sleepAttackChance(owner : Player, opp : Player, baseDmg : Int = 0)
    = setStatusConditionAttack(owner, opp, StatusCondition.ASLEEP, baseDmg, true)

  def confuseAttack(owner : Player, opp : Player, baseDmg : Int = 0)
    = setStatusConditionAttack(owner, opp, StatusCondition.CONFUSED, baseDmg, false)

  def confuseAttackChance(owner : Player, opp : Player, baseDmg : Int = 0)
    = setStatusConditionAttack(owner, opp, StatusCondition.CONFUSED, baseDmg, true)

  def paralyzeAttack(owner : Player, opp : Player, baseDmg : Int = 0)
    = setStatusConditionAttack(owner, opp, StatusCondition.PARALYZED, baseDmg, false)

  def paralyzeAttackChance(owner : Player, opp : Player, baseDmg : Int = 0)
    = setStatusConditionAttack(owner, opp, StatusCondition.PARALYZED, baseDmg, true)

  def poisonAttack(owner : Player, opp : Player, baseDmg : Int = 0)
    = setPoisonStatusAttack(owner, opp, PoisonStatus.POISONED, baseDmg, false)

  def poisonAttackChance(owner : Player, opp : Player, baseDmg : Int = 0)
    = setPoisonStatusAttack(owner, opp, PoisonStatus.POISONED, baseDmg, true)

  private def setStatusConditionAttack(
    owner : Player,
    opp : Player,
    statusCondition : StatusCondition.Value,
    baseDmg : Int,
    shouldFlip : Boolean) {
    if (!shouldFlip || flippedHeads()) {
      owner.active.get.inflictStatus(statusCondition)
    }
    standardAttack(owner, opp, baseDmg)
  }

  private def setPoisonStatusAttack(
    owner : Player,
    opp : Player,
    poisonStatus : PoisonStatus.Value,
    baseDmg : Int,
    shouldFlip : Boolean) {
    if (!shouldFlip || flippedHeads()) {
      owner.active.get.poison(poisonStatus)
    }
    standardAttack(owner, opp, baseDmg)
  }

  def energyDiscardAttack(
        owner : Player,
        opp : Player,
        baseDmg : Int,
        eType : EnergyType.Value,
        cnt : Int = 1) {
    owner.discardEnergyFromCard(owner.active.get, eType, cnt)
    standardAttack(owner, opp, baseDmg)
  }

  def discardAndRecover(owner : Player, eType : EnergyType.Value) {
    val discardedCards = owner.active.get.discardEnergy(eType, 1)
    owner.discardCards(discardedCards)
    owner.active.get.heal(owner.active.get.maxHp)
  }

  def harden(owner : Player) : Unit = owner.active.get.harden = true

  def minimize(owner : Player) : Unit = owner.active.get.minimize = true

  def selfDestruct(owner : Player, opp : Player, mainDmg : Int, selfDmg : Int, benchDmg : Int) {
    for (bc : PokemonCard <- owner.bench.toList.flatten) {
      bc.takeDamage(owner.active, benchDmg)
    }
    for (bc : PokemonCard <- opp.bench.toList.flatten) {
      bc.takeDamage(None, benchDmg)
    }
    owner.active.get.takeDamage(None, selfDmg)
    standardAttack(owner, opp, mainDmg)
  }

  def flippedHeads() = true //Random.nextBoolean()
  
}