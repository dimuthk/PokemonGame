import org.specs2.mutable._

import src.card.energy._
import src.card.energy.EnergyType._
import src.card.pokemon._
import src.card.pokemon.base_set._
import src.card.condition._
import src.move.MoveBuilder._
import src.player.Player

import play.api.test._
import play.api.test.Helpers._

class MoveBuilderSpec extends Specification {
/*
  "calculateDmg()" should {
    "echo baseDmg if no modifications" in {
      calculateDmg(
        setPlayer(new Machop()),
        setPlayer(new Machop()),
        30) mustEqual 30
    }
    "double baseDmg if opponent is weak to attacking type" in {
      calculateDmg(
        setPlayer(new Charmander()),
        setPlayer(new Bulbasaur()),
        10) mustEqual 20
    }
    "reduce baseDmg by 30 if opponent resists attacking type" in {
      calculateDmg(
        setPlayer(new Machop()),
        setPlayer(new Charizard()),
        50) mustEqual 20
    }
    "set baseDmg to 0 if opponent resists and dmg less than 30" in {
      calculateDmg(
        setPlayer(new Machop()),
        setPlayer(new Charizard()),
        20) mustEqual 0
    }
  }

  "standardAttack()" should {
    "apply base damage set from calculateDmg()" in {
      val owner = setPlayer(new Machop())
      val opp = setPlayer(new Machop())
      val dmg = calculateDmg(owner, opp, 30)
      standardAttack(owner, opp, 30)
      opp.active.get.maxHp - opp.active.get.currHp mustEqual dmg
    }
  }

  "poisonAttack()" should {
    "do damage and apply poison status to opponent" in {
      val owner = setPlayer(new Machop())
      val opp = setPlayer(new Machop())
      poisonAttack(owner, opp, 20)
      opp.active.get.maxHp - opp.active.get.currHp mustEqual 20
      opp.active.get.poisonStatus mustEqual Some(PoisonStatus.POISONED)
    }
  }

  "energyDiscardAttack()" should {
    "do damage and discard" in {
      val owner = setPlayer(new Machop())
      owner.active.get.energyCards = List(new FireEnergy())
      val opp = setPlayer(new Machop())
      energyDiscardAttack(owner, opp, 30, FIRE)
      opp.active.get.maxHp - opp.active.get.currHp mustEqual 30
      owner.active.get.energyCards must be empty
    }
    "discard one of multiple" in {
      val owner = setPlayer(new Machop())
      owner.active.get.energyCards = List(new FireEnergy(), new FireEnergy())
      val opp = setPlayer(new Machop())
      energyDiscardAttack(owner, opp, 30, FIRE)
      owner.active.get.energyCards must have size(1)
      owner.active.get.energyCards(0).eType mustEqual FIRE
    }
    "discard multiple of multiple" in {
      val owner = setPlayer(new Machop())
      owner.active.get.energyCards = List.fill(3)(new FireEnergy())
      val opp = setPlayer(new Machop())
      energyDiscardAttack(owner, opp, 30, FIRE, 2)
      owner.active.get.energyCards must have size(1)
      owner.active.get.energyCards(0).eType mustEqual FIRE
    }
    "discard multiple of varying energies" in {
      val owner = setPlayer(new Machop())
      owner.active.get.energyCards = List.fill(3)(new FireEnergy()) ++ List.fill(2)(new GrassEnergy())
      val opp = setPlayer(new Machop())
      energyDiscardAttack(owner, opp, 30, FIRE, 2)
      owner.active.get.energyCards must have size(3)
      owner.active.get.energyCards.filter(_.eType == FIRE) must have size(1)
      owner.active.get.energyCards.filter(_.eType == GRASS) must have size(2)
    }
  }

  "standardAttackPlusExtra()" should {
    "work as standard attack if no extra energy" in {
      val owner = setPlayer(new Machop())
      owner.active.get.energyCards = List(new FireEnergy())
      val opp = setPlayer(new Machop())
      standardAttackPlusExtra(owner, opp, 10, FIRE, 1)
      opp.active.get.maxHp - opp.active.get.currHp mustEqual 10
    }
    "not count different energy types as attack boost" in {
      val owner = setPlayer(new Machop())
      owner.active.get.energyCards = List(new FireEnergy(), new GrassEnergy())
      val opp = setPlayer(new Machop())
      standardAttackPlusExtra(owner, opp, 10, FIRE, 1)
      opp.active.get.maxHp - opp.active.get.currHp mustEqual 10
    }
    "increase attack by 10 if one extra energy" in {
      val owner = setPlayer(new Machop())
      owner.active.get.energyCards = List(new FireEnergy(), new FireEnergy(), new GrassEnergy())
      val opp = setPlayer(new Machop())
      standardAttackPlusExtra(owner, opp, 10, FIRE, 1)
      opp.active.get.maxHp - opp.active.get.currHp mustEqual 20
    }
    "increase attack by 20 if two extra energy" in {
      val owner = setPlayer(new Machop())
      owner.active.get.energyCards = List.fill(3)(new FireEnergy()) ++ List(new GrassEnergy())
      val opp = setPlayer(new Machop())
      standardAttackPlusExtra(owner, opp, 10, FIRE, 1)
      opp.active.get.maxHp - opp.active.get.currHp mustEqual 30
    }
    "not increase attack past 20" in {
      val owner = setPlayer(new Machop())
      owner.active.get.energyCards = List.fill(5)(new FireEnergy()) ++ List(new GrassEnergy())
      val opp = setPlayer(new Machop())
      standardAttackPlusExtra(owner, opp, 10, FIRE, 1)
      opp.active.get.maxHp - opp.active.get.currHp mustEqual 30
    }
  }

  def setPlayer(pc : PokemonCard) : Player = {
    val p : Player = new Player()
    p.active = Some(pc)
    return p
  }*/

}
