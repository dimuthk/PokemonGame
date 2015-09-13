import org.specs2.mutable._

import org.mockito.Mockito._
import org.specs2.mock.Mockito
import src.card.energy._
import src.card.energy.EnergyType._
import src.card.pokemon._
import src.card.pokemon.base_set._
import src.card.condition._
import src.move.MoveBuilder._
import src.player.Player

import play.api.test._
import play.api.test.Helpers._

class MoveBuilderSpec extends Specification with Mockito {

  case class Players(owner : Player, opp : Player)

  def setPlayer() : Player = {
    val p : Player = new Player()
    p.setActive(mock[PokemonCard])
    return p
  }

  "standardAttack" should {
    "deal damage" in {
      val pData = Players(setPlayer(), setPlayer())
      standardAttack(pData.owner, pData.opp, 30)
      there was one(pData.opp.active.get).takeDamage(pData.opp.active, 30, useModifiers = false)
    }
  }

  "ignoreTypesAttack" should {
    "deal damage with ignore modifiers flag" in {
      val pData = Players(setPlayer(), setPlayer())
      standardAttack(pData.owner, pData.opp, 30)
      there was one(pData.opp.active.get).takeDamage(pData.opp.active, 30, useModifiers = true)
    }
  }

  "standardAttackPlusExtra()" should {
    "work as standard attack if no extra energy" in {
      val pData = Players(setPlayer(), setPlayer())
      when(pData.owner.active.get.energyCards) thenReturn List(new FireEnergy())
      standardAttackPlusExtra(pData.owner, pData.opp, 10, FIRE, 1)
      there was one(pData.opp.active.get).takeDamage(pData.opp.active, 10)
    }
  }

  /*"standardAttackPlusExtra()" should {
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
