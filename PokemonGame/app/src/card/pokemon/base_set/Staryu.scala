package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Staryu extends BasicPokemon(
	"Staryu",
	"Staryu-Base-Set-65.jpg",
	Deck.BASE_SET,
	Identifier.STARYU,
	id = 120,
	maxHp = 40,
	firstMove = Some(new Move(
		"Slap",
		1,
		Map(EnergyType.WATER -> 1)) {
			def perform = (owner, opp, args) => standardAttack(owner, opp, 20)
		}),
	energyType = EnergyType.WATER,
	weakness = Some(EnergyType.THUNDER),
	retreatCost = 1)