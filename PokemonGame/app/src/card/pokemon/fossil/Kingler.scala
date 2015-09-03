package src.card.pokemon.fossil

import src.json.Identifier
import src.move._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

import play.api.Logger

class Kingler extends StageOnePokemon(
	"Kingler",
	"Kingler-Fossil-38.jpg",
	Deck.FOSSIL,
	Identifier.KINGLER,
	id = 99,
	maxHp = 60,
	firstMove = Some(new Move(
		"Flail",
		1,
		Map(EnergyType.WATER -> 1)) {
			def perform = (owner, opp, args) => standardAttack(owner, opp, 10 * (owner.active.get.maxHp - owner.active.get.currHp))
	}),
	secondMove = Some(new Move(
		"Crabhammer",
		3,
		Map(EnergyType.WATER -> 2)) {
			def perform = (owner, opp, args) => standardAttack(owner, opp, 40)
	}),
	energyType = EnergyType.WATER,
	weakness = Some(EnergyType.THUNDER),
	retreatCost = 3)