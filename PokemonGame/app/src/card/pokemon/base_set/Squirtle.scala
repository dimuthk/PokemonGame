package src.card.pokemon.base_set

import src.json.Identifier
import src.move._
import src.move.MoveBuilder._
import src.player.Player
import src.board.move.PreventDamageInterpreter
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.pokemon.Withdrawable._
import src.card.Deck

import play.api.Logger

class Squirtle extends BasicPokemon(
	"Squirtle",
	"Squirtle-Base-Set-63.jpg",
	Deck.BASE_SET,
	Identifier.SQUIRTLE,
	id = 7,
	maxHp = 40,
	firstMove = Some(new Move(
		"Bubble",
		1,
		Map(EnergyType.WATER -> 1)) {
			def perform = (owner, opp) => paralyzeChanceAttack(owner, opp, 10)
	}),
	secondMove = Some(new Withdraw(
		"Withdraw",
		2,
		Map(EnergyType.WATER -> 1)) {}),
	energyType = EnergyType.WATER,
	weakness = Some(EnergyType.THUNDER),
	retreatCost = 1) with Withdrawable