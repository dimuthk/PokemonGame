package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Weedle extends BasicPokemon(
	"Weedle",
	"Weedle-Base-Set-69.jpg",
	Deck.BASE_SET,
	Identifier.WEEDLE,
	id = 13,
	maxHp = 40,
	firstMove = Some(new Move(
		"Poison Sting",
		1,
		Map(EnergyType.GRASS -> 1)) {
			def perform = (owner, opp, args) => poisonAttackChance(owner, opp, 10)
		}),
	energyType = EnergyType.GRASS,
	weakness = Some(EnergyType.FIRE),
	retreatCost = 1)