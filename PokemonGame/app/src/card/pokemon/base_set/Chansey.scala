package src.card.pokemon.base_set

import src.json.Identifier
import src.move._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Chansey extends BasicPokemon(
	"Chansey",
	"Chansey-Base-Set-3.jpg",
	Deck.BASE_SET,
	Identifier.CHANSEY,
	id = 113,
	maxHp = 120,
	firstMove = Some(new Withdraw(
		"Scrunch",
		2)),
	secondMove = Some(new Move(
		"Double-edge",
		4) {
			def perform = (owner, opp, args) => attackAndHurtSelf(owner, opp, 80, 80)
		}),
	energyType = EnergyType.COLORLESS,
	weakness = Some(EnergyType.FIGHTING),
	resistance = Some(EnergyType.PSYCHIC),
	retreatCost = 1)