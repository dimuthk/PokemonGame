package src.card.pokemon.base_set

import src.card.condition.StatusCondition
import src.json.Identifier
import src.move._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class HaunterFossil extends StageOnePokemon(
	"Haunter",
	"Haunter-Fossil-6.jpg",
	Deck.FOSSIL,
	Identifier.HAUNTER_FOSSIL,
	id = 93,
	maxHp = 50,
	firstMove = Some(new PassivePokemonPower("Transparency") {}),
	secondMove = Some(new Move(
		"Nightmare",
		2,
		Map(EnergyType.PSYCHIC -> 1)) {
			def perform = (owner, opp, args) => sleepAttack(owner, opp, 10)
		}),
	energyType = EnergyType.PSYCHIC,
	resistance = Some(EnergyType.FIGHTING),
	retreatCost = 0)