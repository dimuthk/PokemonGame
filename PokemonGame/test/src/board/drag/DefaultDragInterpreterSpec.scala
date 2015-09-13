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

	def setUpPlayer(active : Option[PokemonCard] = None, bench : Seq[Option[PokemonCard]] = Nil) : Player = {
		val p = new Player()
		if (active.isDefined) {
			p.setActive(active.get)
		}
		for (i <- 0 until bench.length) {
			if (bench(i).isDefined) {
				p.setBench(bench(i).get, i)
			}
		}
		return p
	}

	"benchToBench()" should {

		"move index1 to index2 if index2 is empty" in {
			val drag = new Machop()
			val owner = setUpPlayer(bench = List(Some(drag), None, None, None, None))
			benchToBench(owner, opp, true, 0, 3, Nil)
			owner.bench(0) mustEqual None
			owner.bench(3) mustEqual Some(drag)
		}
		"swap the cards if both indices are not empty" in {
			val drag : PokemonCard = new Machop()
			val drop : PokemonCard = new Rattata()
			val owner = setUpPlayer(bench = List(Some(drag), None, Some(drop), None, None))
			benchToBench(owner, opp, true, 0, 3, Nil)
			owner.bench(0) mustEqual Some(drop)
			owner.bench(3) mustEqual Some(drag)
		}
		"keep the card in place if the indices are the same" in {
			val drag : PokemonCard = new Machop()
			val owner = setUpPlayer(bench = List(Some(drag), None, None, None, None))
			benchToBench(owner, opp, true, 0, 0, Nil)
			owner.bench(0) mustEqual Some(drag)
		}
	}

	"benchToActive()" should {

		"move bench card to active slot if no active" in {
			val drag : PokemonCard = new Machop()
			val owner = setUpPlayer(bench = List(Some(drag), None, None, None, None))
			benchToActive(owner, opp, true, 0, Nil)
			owner.bench(0) mustEqual None
			owner.active mustEqual Some(drag)
		}
		"do nothing if active doesn't have enough energy to retreat" in {
			val drag : PokemonCard = new Machop()
			val drop : PokemonCard = new Rattata()
			val owner = setUpPlayer(active = Some(drop), bench = List(Some(drag), None, None, None, None))
			benchToActive(owner, opp, true, 0, Nil)
			owner.active mustEqual Some(drag)
			owner.bench(0) mustEqual Some(drop)
		}
		"swap bench and active and discard retreat cost if active has enough energy" in {
			val drop : PokemonCard = new Machop()
			val drag : PokemonCard = new Rattata()
			val owner = setUpPlayer(active = Some(drop), bench = List(Some(drag), None, None, None, None))
			drop.attachEnergy(List(new FireEnergy(), new FireEnergy()))
			benchToActive(owner, opp, true, 0, Nil)
			owner.active mustEqual Some(drag)
			owner.bench(0) mustEqual Some(drop)
			drop.energyCards must have size(1)
			drop.energyCards.filter(_.eType == EnergyType.FIRE) must have size(1)
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
		"swap bench and active and discard retreat cost if active has enough energy" in {
			val machop : PokemonCard = new Machop()
			val rattata : PokemonCard = new Rattata()
			val owner : Player = new Player()
			owner.setActive(machop)
			owner.active.get.attachEnergy(List(new FireEnergy(), new FireEnergy()))
			owner.bench(0) = Some(rattata)
			activeToBench(owner, opp, true, 0, Nil)
			owner.active mustEqual Some(rattata)
			owner.bench(0) mustEqual Some(machop)
			owner.bench(0).get.energyCards must have size(1)
			owner.bench(0).get.energyCards.filter(_.eType == EnergyType.FIRE) must have size(1)
		}
	}

}