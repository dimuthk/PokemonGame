package src.card.pokemon.fossil

import src.json.Identifier
import src.move._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

import play.api.Logger

class Kabuto extends StageOnePokemon(
	"Kabuto",
	"Kabuto-Fossil-50.jpg",
	Deck.FOSSIL,
	Identifier.KABUTO,
	id = 140,
	maxHp = 30,
	firstMove = Some(new PassivePokemonPower("Kabuto Armor") {}),
	secondMove = Some(new Move(
      "Scratch",
      1) {
        def perform = (owner, opp, args) => standardAttack(owner, opp, 10)
      }),
	energyType = EnergyType.FIGHTING,
	weakness = Some(EnergyType.GRASS),
	retreatCost = 1) {

	override def takeDamage(attacker : Option[PokemonCard], dmg : Int) {
		// TODO
		super.takeDamage(attacker, dmg)
	}
}