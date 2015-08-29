package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Caterpie extends BasicPokemon(
	"Caterpie",
	"Caterpie-Base-Set-45.jpg",
	Deck.BASE_SET,
	Identifier.CATERPIE,
	id = 10,
	maxHp = 40,
	firstMove = Some(new Move(
		"String Shot",
		1,
		Map(EnergyType.GRASS -> 1)) {
			def perform = (owner, opp) => paralyzeChanceAttack(owner, opp, 10)
		}),
	energyType = EnergyType.GRASS,
	weakness = Some(EnergyType.FIRE),
	retreatCost = 1)