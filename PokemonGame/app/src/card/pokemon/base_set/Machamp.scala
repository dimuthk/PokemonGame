package src.card.pokemon.base_set

import src.json.Identifier
import src.move._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Machamp extends StageTwoPokemon(
	"Machamp",
	"Machamp-Base-Set-8.jpg",
	Deck.BASE_SET,
	Identifier.MACHAMP,
	id = 68,
	maxHp = 100,
	firstMove = Some(new PassivePokemonPower("Strikes Back") {}),
	secondMove = Some(new Move(
		"Seismic Toss",
		4,
		Map(EnergyType.FIGHTING -> 3)) {
			def perform = (owner, opp, args) => standardAttack(owner, opp, 60)
		}),
	energyType = EnergyType.FIGHTING,
	weakness = Some(EnergyType.PSYCHIC),
	retreatCost = 3) {

	override def takeDamage(attacker : Option[PokemonCard], amount : Int) : Unit = {
		super.takeDamage(attacker, amount)
		if (attacker.isDefined) {
			attacker.get.takeDamage(Some(this), 10)
		}
  	}

}