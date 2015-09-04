package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Hitmonchan extends BasicPokemon(
	"Hitmonchan",
	"Hitmonchan-Base-Set-7.jpg",
	Deck.BASE_SET,
	Identifier.HITMONCHAN,
	id = 107,
	maxHp = 70,
	firstMove = Some(new Move(
		"Jab",
		1,
		Map(EnergyType.FIGHTING -> 1)) {
			def perform = (owner, opp, args) => standardAttack(owner, opp, 20)
		}),
	secondMove = Some(new Move(
		"Special Punch",
		3,
		Map(EnergyType.FIGHTING -> 2)) {
			def perform = (owner, opp, args) => standardAttack(owner, opp, 40)
		}),
	energyType = EnergyType.FIGHTING,
	weakness = Some(EnergyType.PSYCHIC),
	retreatCost = 2)