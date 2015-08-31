package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Machop extends BasicPokemon(
	"Machop",
	"Machop-Base-Set-52.jpg",
	Deck.BASE_SET,
	Identifier.MACHOP,
	id = 66,
	maxHp = 50,
	firstMove = Some(new Move(
		"Low Kick",
		1,
		Map(EnergyType.FIGHTING -> 1)) {
			def perform = (owner, opp, args) => standardAttack(owner, opp, 20)
		}),
	energyType = EnergyType.FIGHTING,
	weakness = Some(EnergyType.PSYCHIC),
	retreatCost = 1)