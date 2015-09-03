package src.card.pokemon.fossil

import src.json.Identifier
import src.move._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

import play.api.Logger

class Cloyster extends StageOnePokemon(
	"Cloyster",
	"Cloyster-Fossil-32.jpg",
	Deck.FOSSIL,
	Identifier.CLOYSTER,
	id = 91,
	maxHp = 50,
	firstMove = Some(new Move(
		"Clamp",
		2,
		Map(EnergyType.WATER -> 2)) {
			def perform = (owner, opp, args) => flippedHeads() match {
				case true => paralyzeAttack(owner, opp, 30)
				case false => None
			}
	}),
	secondMove = Some(new Move(
		"Spike Cannon",
		2,
		Map(EnergyType.WATER -> 2)) {
			def perform = (owner, opp, args) => multipleHitAttack(owner, opp, 30, 2)
	}),
	energyType = EnergyType.WATER,
	weakness = Some(EnergyType.THUNDER),
	retreatCost = 2)