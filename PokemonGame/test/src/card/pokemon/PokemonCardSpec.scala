package src.card.pokemon

import org.specs2.mutable._
import org.mockito.Mockito._
import org.specs2.mock.Mockito

import src.board.PlayerData
import src.card.Card
import src.card.energy._
import src.card.energy.EnergyType._
import src.card.pokemon._
import src.card.pokemon.base_set._
import src.card.pokemon.jungle._
import src.card.pokemon.fossil._
import src.card.condition._
import src.board.drag.DefaultDragInterpreter._
import src.player.Player

import play.api.test._
import play.api.test.Helpers._

class PokemonCardSpec extends Specification with Mockito {

	var underTest : PokemonCard = null

	"takeDamage" should {

		var dmg = 40

		"do standard damage" in {
			underTest = new Charizard()
			underTest.takeDamage(Some(new Rattata()), dmg)
			underTest.currHp mustEqual underTest.maxHp - dmg
		}
		"do double damage if weak to attacker" in {
			underTest = new Charizard()
			underTest.takeDamage(Some(new Squirtle()), dmg)
			underTest.currHp mustEqual underTest.maxHp - (dmg * 2)
		}
		"reduce damage by 30 if resists" in {
			underTest = new Charizard()
			underTest.takeDamage(Some(new Machop()), dmg)
			underTest.currHp mustEqual underTest.maxHp - (dmg - 30)
		}
		"do no damage if resists and base dmg less than 30" in {
			underTest = new Charizard()
			underTest.takeDamage(Some(new Machop()), 20)
			underTest.currHp mustEqual underTest.maxHp
		}
		"don't do more damage than curr hp" in {
			underTest = new Charizard()
			underTest.takeDamage(Some(new Rattata()), 40)
			underTest.takeDamage(Some(new Rattata()), 100)
			underTest.currHp mustEqual 0
		}
	}

}