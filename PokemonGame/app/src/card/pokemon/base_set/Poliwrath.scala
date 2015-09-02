package src.card.pokemon.base_set

import src.board.intermediary.OpponentCardInterface
import src.json.Identifier
import src.move._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck
import play.api.Logger

class Poliwrath extends StageTwoPokemon(
	"Poliwrath",
	"Poliwrath-Base-Set-13.jpg",
	Deck.BASE_SET,
	Identifier.POLIWRATH,
	id = 62,
	maxHp = 90,
	firstMove = Some(new Move(
		"Water Gun",
		3,
		Map(EnergyType.WATER -> 2)) {
			def perform = (owner, opp, args) => standardAttackPlusExtra(owner, opp, 30, EnergyType.WATER, 3)
		}),
	secondMove = Some(new HyperBeam(
		"Whirlpool",
		40,
		4,
		Map(EnergyType.WATER -> 2))),
	energyType = EnergyType.WATER,
	weakness = Some(EnergyType.GRASS),
	retreatCost = 3)