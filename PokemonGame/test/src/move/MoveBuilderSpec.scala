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
    "not count different energy types as attack boost" in {
      val pData = Players(setPlayer(), setPlayer())
      when(pData.owner.active.get.energyCards) thenReturn List(new FireEnergy(), new GrassEnergy())
      standardAttackPlusExtra(pData.owner, pData.opp, 10, FIRE, 1)
      there was one(pData.opp.active.get).takeDamage(pData.opp.active, 10)
    }
    "increase attack by 10 if one extra energy" in {
      val pData = Players(setPlayer(), setPlayer())
      when(pData.owner.active.get.energyCards) thenReturn List.fill(2)(new FireEnergy()) ++ List(new GrassEnergy())
      standardAttackPlusExtra(pData.owner, pData.opp, 10, FIRE, 1)
      there was one(pData.opp.active.get).takeDamage(pData.opp.active, 20)
    }
    "increase attack by 20 if two extra energy" in {
      val pData = Players(setPlayer(), setPlayer())
      when(pData.owner.active.get.energyCards) thenReturn List.fill(3)(new FireEnergy()) ++ List(new GrassEnergy())
      standardAttackPlusExtra(pData.owner, pData.opp, 10, FIRE, 1)
      there was one(pData.opp.active.get).takeDamage(pData.opp.active, 30)
    }
    "not increase attack past 20" in {
      val pData = Players(setPlayer(), setPlayer())
      when(pData.owner.active.get.energyCards) thenReturn List.fill(5)(new FireEnergy()) ++ List(new GrassEnergy())
      standardAttackPlusExtra(pData.owner, pData.opp, 10, FIRE, 1)
      there was one(pData.opp.active.get).takeDamage(pData.opp.active, 30)
    }
  }

}
