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

	val player : Player = new Player()
	val pc1 : PokemonCard = new Machop()
	val pc2 : PokemonCard = new Rattata()

	"benchToBench()" should {
		"move index1 to index2 if index2 is empty" in {
			player.bench(0) = Some(pc1)
			benchToBench(player, 0, 3)
			player.bench(0) mustEqual None
			player.bench(3) mustEqual Some(pc1)
		}
		"swap the cards if both indices are not empty" in {
			player.bench(0) = Some(pc1)
			player.bench(3) = Some(pc2)
			benchToBench(player, 0, 3)
			player.bench(0) mustEqual Some(pc2)
			player.bench(3) mustEqual Some(pc1)
		}
		"keep the card in place if the indices are the same" in {
			player.bench(0) = Some(pc1)
			benchToBench(player, 0, 0)
			player.bench(0) mustEqual Some(pc1)
		}
	}

	"benchToActive()" should {
		"move bench card to active slot if no active" {
			player.bench(0) = Some(pc1)
			benchToActive(player, 0)
			player.bench(0) mustEqual None
			player.active mustEqual Some(pc1)
		}

	}

}