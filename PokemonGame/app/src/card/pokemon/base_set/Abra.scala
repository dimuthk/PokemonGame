package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Abra extends BasicPokemon(
	"Abra",
	"Abra-Base-Set-43.jpg",
	Deck.BASE_SET,
	Identifier.ABRA,
	id = 63,
	maxHp = 30,
	firstMove = Some(new Move(
		"Psyshock",
		1,
		Map(EnergyType.PSYCHIC -> 1)) {
			def perform = (owner, opp, args) => paralyzeAttackChance(owner, opp, 10)
		}),
	energyType = EnergyType.PSYCHIC,
	weakness = Some(EnergyType.PSYCHIC),
	retreatCost = 0)