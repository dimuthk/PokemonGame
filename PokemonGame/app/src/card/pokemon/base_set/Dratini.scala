package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Dratini extends BasicPokemon(
	"Dratini",
	"Dratini-Base-Set-26.jpg",
	Deck.BASE_SET,
	Identifier.DRATINI,
	id = 147,
	maxHp = 40,
	firstMove = Some(new Move(
		"Pound",
		1) {
			def perform = (owner, opp, args) => standardAttack(owner, opp, 10)
		}),
	energyType = EnergyType.COLORLESS,
	resistance = Some(EnergyType.PSYCHIC),
	retreatCost = 1)