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

	val opp : Player = new Player()

	"benchToBench()" should {

		"move index1 to index2 if index2 is empty" in {
			val machop : PokemonCard = new Machop()
			val rattata : PokemonCard = new Rattata()
			val owner : Player = new Player()
			owner.bench(0) = Some(machop)
			benchToBench(owner, opp, true, 0, 3, Nil)
			owner.bench(0) mustEqual None
			owner.bench(3) mustEqual Some(machop)
		}
		"swap the cards if both indices are not empty" in {
			val machop : PokemonCard = new Machop()
			val rattata : PokemonCard = new Rattata()
			val owner : Player = new Player()
			owner.bench(0) = Some(machop)
			owner.bench(3) = Some(rattata)
			benchToBench(owner, opp, true, 0, 3, Nil)
			owner.bench(0) mustEqual Some(rattata)
			owner.bench(3) mustEqual Some(machop)
		}
		"keep the card in place if the indices are the same" in {
			val machop : PokemonCard = new Machop()
			val rattata : PokemonCard = new Rattata()
			val owner : Player = new Player()
			owner.bench(0) = Some(machop)
			benchToBench(owner, opp, true, 0, 0, Nil)
			owner.bench(0) mustEqual Some(machop)
		}
	}

	"benchToActive()" should {

		"move bench card to active slot if no active" in {
			val machop : PokemonCard = new Machop()
			val rattata : PokemonCard = new Rattata()
			val owner : Player = new Player()
			owner.bench(0) = Some(machop)
			benchToActive(owner, opp, true, 0, Nil)
			owner.bench(0) mustEqual None
			owner.active mustEqual Some(machop)
		}
		"do nothing if active doesn't have enough energy to retreat" in {
			val machop : PokemonCard = new Machop()
			val rattata : PokemonCard = new Rattata()
			val owner : Player = new Player()
			owner.setActive(machop)
			owner.bench(0) = Some(rattata)
			benchToActive(owner, opp, true, 0, Nil)
			owner.active mustEqual Some(machop)
			owner.bench(0) mustEqual Some(rattata)
		}
		"do nothing if active doesn't have enough energy to retreat even with ambiguity" in {
			val charizard : PokemonCard = new Charizard()
			val rattata : PokemonCard = new Rattata()
			val owner : Player = new Player()
			owner.setActive(charizard)
			owner.active.get.energyCards = List(new FireEnergy(), new PsychicEnergy())
			val res = benchToActive(owner, opp, true, 0, Nil)
			res mustEqual None
			owner.bench(0) = Some(rattata)
			owner.active mustEqual Some(charizard)
			owner.bench(0) mustEqual Some(rattata)
		}
		"swap bench and active and discard retreat cost if active has enough energy" in {
			val machop : PokemonCard = new Machop()
			val rattata : PokemonCard = new Rattata()
			val owner : Player = new Player()
			owner.setActive(machop)
			owner.active.get.energyCards = List(new FireEnergy(), new FireEnergy())
			owner.bench(0) = Some(rattata)
			benchToActive(owner, opp, true, 0, Nil)
			owner.active mustEqual Some(rattata)
			owner.bench(0) mustEqual Some(machop)
			owner.bench(0).get.energyCards must have size(1)
			owner.bench(0).get.energyCards.filter(_.eType == EnergyType.FIRE) must have size(1)
		}
	}

	"activeToBench()" should {

		"do nothing if active doesn't have enough energy to retreat" in {
			val machop : PokemonCard = new Machop()
			val rattata : PokemonCard = new Rattata()
			val owner : Player = new Player()
			owner.setActive(machop)
			owner.bench(0) = Some(rattata)
			benchToActive(owner, opp, true, 0, Nil)
			owner.active mustEqual Some(machop)
			owner.bench(0) mustEqual Some(rattata)
		}
		"move active to bench if no bench" in {
			val machop : PokemonCard = new Machop()
			val rattata : PokemonCard = new Rattata()
			val owner : Player = new Player()
			owner.setActive(rattata)
			owner.bench(0) = None
			activeToBench(owner, opp, true, 0, Nil)
			owner.active mustEqual None
			owner.bench(0) mustEqual Some(rattata)
		}
	}

}