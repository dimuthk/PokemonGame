package src.card.pokemon.fossil

import src.json.Identifier
import src.move._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

import play.api.Logger

class Shellder extends BasicPokemon(
	"Shellder",
	"Shellder-Fossil-54.jpg",
	Deck.FOSSIL,
	Identifier.SHELLDER,
	id = 90,
	maxHp = 30,
	firstMove = Some(new Move(
		"Supersonic",
		1,
		Map(EnergyType.WATER -> 1)) {
			def perform = (owner, opp, args) => confuseAttackChance(owner, opp)
	}),
	secondMove = Some(new Withdraw(
		"Hide in Shell",
		1,
		Map(EnergyType.WATER -> 1))),
	energyType = EnergyType.WATER,
	weakness = Some(EnergyType.THUNDER),
	retreatCost = 1)