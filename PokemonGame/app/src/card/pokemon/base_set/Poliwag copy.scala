package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Poliwag extends BasicPokemon(
	"Poliwag",
	"Poliwag-Base-Set-59.jpg",
	Deck.BASE_SET,
	Identifier.POLIWAG,
	id = 60,
	maxHp = 40,
	firstMove = Some(new Move(
		"Water Gun",
		1,
		Map(EnergyType.WATER -> 1)) {
			def perform = (owner, opp, args) => standardAttackPlusExtra(owner, opp, 10, EnergyType.WATER, 1)
		}),
	energyType = EnergyType.WATER,
	weakness = Some(EnergyType.GRASS),
	retreatCost = 1)