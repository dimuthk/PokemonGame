package src.board.drag

import org.specs2.mutable._

import src.card.energy._
import src.card.energy.EnergyType._
import src.card.pokemon._
import src.card.pokemon.base_set._
import src.card.condition._
import src.board.drag.DefaultDragInterpreter._
import src.player.Player

import play.api.test._
import play.api.test.Helpers._

class DefaultDragInterpreterSpec extends Specification {

	"benchToBench()" should {

		val machop : PokemonCard = new Machop()
		val rattata : PokemonCard = new Rattata()
		val player : Player = new Player()

		"move index1 to index2 if index2 is empty" in {
			player.bench(0) = Some(machop)
			benchToBench(player, 0, 3)
			player.bench(0) mustEqual None
			player.bench(3) mustEqual Some(machop)
		}
		"swap the cards if both indices are not empty" in {
			player.bench(0) = Some(machop)
			player.bench(3) = Some(rattata)
			benchToBench(player, 0, 3)
			player.bench(0) mustEqual Some(rattata)
			player.bench(3) mustEqual Some(machop)
		}
		"keep the card in place if the indices are the same" in {
			player.bench(0) = Some(machop)
			benchToBench(player, 0, 0)
			player.bench(0) mustEqual Some(machop)
		}
	}

	"benchToActive()" should {

		val machop : PokemonCard = new Machop()
		val rattata : PokemonCard = new Rattata()
		val player : Player = new Player()

		"move bench card to active slot if no active" in {
			player.bench(0) = Some(machop)
			benchToActive(player, 0)
			player.bench(0) mustEqual None
			player.active mustEqual Some(machop)
		}
		"do nothing if active doesn't have enough energy to retreat" in {
			player.active = Some(machop)
			player.bench(0) = Some(rattata)
			benchToActive(player, 0)
			player.active mustEqual Some(machop)
			player.bench(0) mustEqual Some(rattata)
		}
		"swap bench and active and discard retreat cost if active has enough energy" in {
			player.active = Some(machop)
			player.active.get.energyCards = List(new FireEnergy(), new FireEnergy())
			player.bench(0) = Some(rattata)
			benchToActive(player, 0)
			player.active mustEqual Some(rattata)
			player.bench(0) mustEqual Some(machop)
			player.bench(0).get.energyCards must have size(1)
			player.bench(0).get.energyCards.filter(_.eType == EnergyType.FIRE) must have size(1)
		}
	}

	"activeToBench()" should {

		val machop : PokemonCard = new Machop()
		val rattata : PokemonCard = new Rattata()
		val player : Player = new Player()

		"do nothing if active doesn't have enough energy to retreat" in {
			player.active = Some(machop)
			player.bench(0) = Some(rattata)
			benchToActive(player, 0)
			player.active mustEqual Some(machop)
			player.bench(0) mustEqual Some(rattata)
		}
		"move active to bench if no bench" in {
			player.active = Some(rattata)
			player.bench(0) = None
			activeToBench(player, 0)
			player.active mustEqual None
			player.bench(0) mustEqual Some(rattata)
		}
	}

}