package src.card.pokemon.base_set

import src.json.Identifier
import src.move._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class MrMime extends BasicPokemon(
	"Mr. Mime",
	"Mr.-Mime-Jungle-6.jpg",
	Deck.JUNGLE,
	Identifier.MR_MIME,
	id = 122,
	maxHp = 40,
	firstMove = Some(new PassivePokemonPower("Invisible Wall") {}),
	secondMove = Some(new Move(
		"Meditate",
        2,
        Map(EnergyType.PSYCHIC -> 1)) {
			def perform = (owner, opp, args) => standardAttack(owner, opp, 10 + 10 * (opp.active.get.maxHp - opp.active.get.currHp))
        }),
	energyType = EnergyType.PSYCHIC,
	weakness = Some(EnergyType.PSYCHIC),
	retreatCost = 1) {

	override def takeDamage(
      attacker : Option[PokemonCard],
      baseAmount : Int,
      useModifiers : Boolean = true,
      ignoreTypes : Boolean = false) : Int = baseAmount match {
		case x : Int if x >= 30 => 0
		case _ => super.takeDamage(attacker, baseAmount, useModifiers, ignoreTypes)
	}

}