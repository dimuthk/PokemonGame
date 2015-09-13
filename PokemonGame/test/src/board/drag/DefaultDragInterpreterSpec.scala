package src.board.drag

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

class DefaultDragInterpreterSpec extends Specification with Mockito {

	def setUpPlayer(
			active : Option[PokemonCard] = None,
			bench : Seq[PokemonCard] = Nil,
			hand : Seq[Card] = Nil) : PlayerData = {
		val p = mock[Player]
		when(p.active) thenReturn active
		val _bench : Array[Option[PokemonCard]] = Array.fill(5) { None }
		for (i <- 0 until bench.length) {
			_bench(i) = Some(bench(i))
		}
		when(p.bench) thenReturn _bench
		when(p.hand) thenReturn hand
		return PlayerData(p, new Player(), true)
	}

	"benchToBench" should {
		"move index1 to index2 if index2 is empty" in {
			val pData = setUpPlayer(bench = List(new Machop()))
			handleDrag(pData, BenchToBench(0, 3), Nil)
			there was one(pData.owner).swapBenchCards(0, 3)
		}
		"swap the cards if both indices are not empty" in {
			val pData = setUpPlayer(bench = List(new Machop(), new Rattata()))
			handleDrag(pData, BenchToBench(0, 1), Nil)
			there was one(pData.owner).swapBenchCards(0, 1)
		}
		"swap the cards if both indices are not empty" in {
			val pData = setUpPlayer(bench = List(new Machop(), new Rattata()))
			handleDrag(pData, BenchToBench(0, 1), Nil)
			there was one(pData.owner).swapBenchCards(0, 1)
		}
		"keep the card in place if the indices are the same" in {
			val pData = setUpPlayer(bench = List(new Machop()))
			handleDrag(pData, BenchToBench(0, 0), Nil)
			there was one(pData.owner).swapBenchCards(0, 0)
		}
	}

	"benchToActive" should {
		"move bench card to active slot if no active" in {
			val pData = setUpPlayer(bench = List(new Machop()))
			handleDrag(pData, BenchToActive(0), Nil)
			there was one(pData.owner).swapActiveAndBench(0)
		}
		"do nothing if active doesn't have enough energy to retreat" in {
			val pData = setUpPlayer(active = Some(new Machop()), bench = List(new Rattata()))
			handleDrag(pData, BenchToActive(0), Nil)
			there was no(pData.owner).swapActiveAndBench(0)
		}
		"swap bench and active and discard retreat cost if active has enough energy" in {
			val active = new Machop()
			active.attachEnergy(List(new FireEnergy(), new FireEnergy()))
			val pData = setUpPlayer(active = Some(active), bench = List(new Rattata()))
			handleDrag(pData, BenchToActive(0), Nil)
			there was one(pData.owner).swapActiveAndBench(0)
			there was one(pData.owner).discardEnergyFromCard(active, cnt = active.retreatCost)
		}
		"discount retreat with dodrio" in {
			val active = new Charizard()
			active.attachEnergy(List(new FireEnergy(), new FireEnergy()))
			val pData = setUpPlayer(active = Some(active), bench = List(new Dodrio()))
			handleDrag(pData, BenchToActive(0), Nil)
			there was one(pData.owner).discardEnergyFromCard(active, cnt = active.retreatCost - 1)
		}
		"retreat for free if dodrios exceed retreat cost" in {
			val active = new Machop()
			val pData = setUpPlayer(active = Some(active), bench = List(new Dodrio(), new Dodrio()))
			handleDrag(pData, BenchToActive(0), Nil)
			there was one(pData.owner).swapActiveAndBench(0)
			there was one(pData.owner).discardEnergyFromCard(active, cnt = 0)
		}
		"retreat specific if args" in {
			val active = new Venusaur()
			active.attachEnergy(List(new FireEnergy(), new PsychicEnergy(), new PsychicEnergy()))
			val pData = setUpPlayer(active = Some(active), bench = List(new Machop()))
			handleDrag(pData, BenchToActive(0), List("0", "2"))
			there was one(pData.owner).swapActiveAndBench(0)
			there was one(pData.owner).discardSpecificEnergyFromCard(active, List(0, 2))
		}
	}

	"benchToActive additional" should {
		"request if multiple energy" in {
			val active = new Machop()
			active.attachEnergy(List(new FireEnergy(), new PsychicEnergy()))
			val pData = setUpPlayer(active = Some(active), bench = List(new Rattata()))
			requestAdditional(pData, BenchToActive(0), Nil).isDefined must beTrue
		}
		"not request if args exist" in {
			val active = new Machop()
			active.attachEnergy(List(new FireEnergy(), new PsychicEnergy()))
			val pData = setUpPlayer(active = Some(active), bench = List(new Rattata()))
			requestAdditional(pData, BenchToActive(0), List("0")).isDefined must beFalse
		}
		"not request if multiple but not enough energy overall" in {
			val active = new Charizard()
			active.attachEnergy(List(new FireEnergy(), new PsychicEnergy()))
			val pData = setUpPlayer(active = Some(active), bench = List(new Rattata()))
			requestAdditional(pData, BenchToActive(0), Nil).isDefined must beFalse
		}
		"request if dodrio discount allows for retreat cost to be met" in {
			val active = new Charizard()
			active.attachEnergy(List(new FireEnergy(), new PsychicEnergy()))
			val pData = setUpPlayer(active = Some(active), bench = List(new Dodrio()))
			requestAdditional(pData, BenchToActive(0), Nil).isDefined must beTrue
		}
		"not request if multiple but retreat cost is zero" in {
			val active = new Rattata()
			active.attachEnergy(List(new FireEnergy(), new PsychicEnergy()))
			val pData = setUpPlayer(active = Some(active), bench = List(new Rattata()))
			requestAdditional(pData, BenchToActive(0), Nil).isDefined must beFalse
		}
		"not request if dodrio discount makes retreat free" in {
			val active = new Machop()
			active.attachEnergy(List(new FireEnergy(), new PsychicEnergy()))
			val pData = setUpPlayer(active = Some(active), bench = List(new Dodrio()))
			requestAdditional(pData, BenchToActive(0), Nil).isDefined must beFalse
		}
	}

	"activeToBench" should {
		"do nothing if active doesn't have enough energy to retreat" in {
			val pData = setUpPlayer(active = Some(new Machop()), bench = List(new Rattata()))
			handleDrag(pData, ActiveToBench(0), Nil)
			there was no(pData.owner).swapActiveAndBench(0)
		}
		"move active to bench if no bench" in {
			val pData = setUpPlayer(active = Some(new Rattata()))
			handleDrag(pData, ActiveToBench(0), Nil)
			there was one(pData.owner).swapActiveAndBench(0)
		}
		"swap bench and active and discard retreat cost if active has enough energy" in {
			val active = new Machop()
			active.attachEnergy(List(new FireEnergy(), new FireEnergy()))
			val pData = setUpPlayer(active = Some(active), bench = List(new Rattata()))
			handleDrag(pData, ActiveToBench(0), Nil)
			there was one(pData.owner).swapActiveAndBench(0)
			there was one(pData.owner).discardEnergyFromCard(active, cnt = active.retreatCost)
		}
		"discount retreat with dodrio" in {
			val active = new Charizard()
			active.attachEnergy(List(new FireEnergy(), new FireEnergy()))
			val pData = setUpPlayer(active = Some(active), bench = List(new Dodrio()))
			handleDrag(pData, ActiveToBench(0), Nil)
			there was one(pData.owner).discardEnergyFromCard(active, cnt = active.retreatCost - 1)
		}
		"retreat for free if dodrios exceed retreat cost" in {
			val active = new Machop()
			val pData = setUpPlayer(active = Some(active), bench = List(new Dodrio(), new Dodrio()))
			handleDrag(pData, ActiveToBench(0), Nil)
			there was one(pData.owner).swapActiveAndBench(0)
			there was one(pData.owner).discardEnergyFromCard(active, cnt = 0)
		}
		"retreat specific if args" in {
			val active = new Venusaur()
			active.attachEnergy(List(new FireEnergy(), new PsychicEnergy(), new PsychicEnergy()))
			val pData = setUpPlayer(active = Some(active), bench = List(new Machop()))
			handleDrag(pData, ActiveToBench(0), List("0", "2"))
			there was one(pData.owner).swapActiveAndBench(0)
			there was one(pData.owner).discardSpecificEnergyFromCard(active, List(0, 2))
		}
	}

	"activeToBench additional" should {
		"request if multiple energy" in {
			val active = new Machop()
			active.attachEnergy(List(new FireEnergy(), new PsychicEnergy()))
			val pData = setUpPlayer(active = Some(active), bench = List(new Rattata()))
			requestAdditional(pData, ActiveToBench(0), Nil).isDefined must beTrue
		}
		"not request if args exist" in {
			val active = new Machop()
			active.attachEnergy(List(new FireEnergy(), new PsychicEnergy()))
			val pData = setUpPlayer(active = Some(active), bench = List(new Rattata()))
			requestAdditional(pData, ActiveToBench(0), List("0")).isDefined must beFalse
		}
		"not request if multiple but not enough energy overall" in {
			val active = new Charizard()
			active.attachEnergy(List(new FireEnergy(), new PsychicEnergy()))
			val pData = setUpPlayer(active = Some(active), bench = List(new Rattata()))
			requestAdditional(pData, ActiveToBench(0), Nil).isDefined must beFalse
		}
		"request if dodrio discount allows for retreat cost to be met" in {
			val active = new Charizard()
			active.attachEnergy(List(new FireEnergy(), new PsychicEnergy()))
			val pData = setUpPlayer(active = Some(active), bench = List(new Dodrio()))
			requestAdditional(pData, ActiveToBench(0), Nil).isDefined must beTrue
		}
		"not request if multiple but retreat cost is zero" in {
			val active = new Rattata()
			active.attachEnergy(List(new FireEnergy(), new PsychicEnergy()))
			val pData = setUpPlayer(active = Some(active), bench = List(new Rattata()))
			requestAdditional(pData, ActiveToBench(0), Nil).isDefined must beFalse
		}
		"not request if dodrio discount makes retreat free" in {
			val active = new Machop()
			active.attachEnergy(List(new FireEnergy(), new PsychicEnergy()))
			val pData = setUpPlayer(active = Some(active), bench = List(new Dodrio()))
			requestAdditional(pData, ActiveToBench(0), Nil).isDefined must beFalse
		}
	}

	"handToActive with basic pokemon" should {
		"move to active if active is empty" in {
			val pData = setUpPlayer(hand = List(new Machop()))
			handleDrag(pData, HandToActive(0), Nil)
			there was one(pData.owner).moveHandToActive(0)
		}
		"do nothing if active not empty" in {
			val pData = setUpPlayer(active = Some(new Rattata()), hand = List(new Machop()))
			handleDrag(pData, HandToActive(0), Nil)
			there was no(pData.owner).moveHandToActive(0)
		}
	}

	"handToActive with evolved pokemon" should {
		"evolve active if this is evolution of active" in {
			val pData = setUpPlayer(active = Some(new Machop()), hand = List(new Machoke()))
			handleDrag(pData, HandToActive(0), Nil)
			there was one(pData.owner).evolveActiveCard(0)
		}
		"do nothing if active is empty" in {
			val pData = setUpPlayer(hand = List(new Machoke()))
			handleDrag(pData, HandToActive(0), Nil)
			there was no(pData.owner).moveHandToActive(0)
			there was no(pData.owner).evolveActiveCard(0)
		}
		"do nothing if evolution doesn't match" in {
			val pData = setUpPlayer(active = Some(new Rattata()), hand = List(new Machoke()))
			handleDrag(pData, HandToActive(0), Nil)
			there was no(pData.owner).evolveActiveCard(0)
		}
	}

	"handToActive with energy card" should {
		"attach energy if active is not empty" in {
			val active = new Machop()
			val pData = setUpPlayer(active = Some(active), hand = List(new FireEnergy()))
			handleDrag(pData, HandToActive(0), Nil)
			there was one(pData.owner).attachEnergyFromHand(active, 0)
		}
		"do nothing if active is empty" in {
			val pData = setUpPlayer(hand = List(new Machoke()))
			handleDrag(pData, HandToActive(0), Nil)
			there was no(pData.owner).attachEnergyFromHand(any[PokemonCard], any[Int])
		}
	}

	"handToBench with basic pokemon" should {
		"move to bench if bench slot is empty" in {
			val pData = setUpPlayer(hand = List(new Machop()))
			handleDrag(pData, HandToBench(0, 0), Nil)
			there was one(pData.owner).moveHandToBench(0, 0)
		}
		"do nothing if bench slot is not empty" in {
			val pData = setUpPlayer(bench = List(new Rattata()), hand = List(new Machop()))
			handleDrag(pData, HandToBench(0, 0), Nil)
			there was no(pData.owner).moveHandToBench(0, 0)
		}
	}

	"handToBench with evolved pokemon" should {
		"evolve bench if this is evolution of bench" in {
			val pData = setUpPlayer(bench = List(new Machop()), hand = List(new Machoke()))
			handleDrag(pData, HandToBench(0, 0), Nil)
			there was one(pData.owner).evolveBenchCard(0, 0)
		}
		"do nothing if bench slot is empty" in {
			val pData = setUpPlayer(hand = List(new Machoke()))
			handleDrag(pData, HandToBench(0, 0), Nil)
			there was no(pData.owner).moveHandToBench(0, 0)
			there was no(pData.owner).evolveBenchCard(0, 0)
		}
		"do nothing if evolution doesn't match" in {
			val pData = setUpPlayer(bench = List(new Rattata()), hand = List(new Machoke()))
			handleDrag(pData, HandToBench(0, 0), Nil)
			there was no(pData.owner).evolveBenchCard(0, 0)
		}
	}

}