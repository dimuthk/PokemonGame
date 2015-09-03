package src.card.pokemon.base_set

import src.json.Identifier
import src.move._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Rapidash extends StageOnePokemon(
	"Rapidash",
	"Rapidash-Jungle-44.jpg",
	Deck.JUNGLE,
	Identifier.RAPIDASH,
	id = 78,
	maxHp = 70,
	firstMove = Some(new Move(
		"Stomp",
		2) {
			def perform = (owner, opp, args) => flippedHeads() match {
				case true => standardAttack(owner, opp, 20)
				case false => standardAttack(owner, opp, 30)
			}
		}),
	secondMove = Some(new Agility(
		"Agility",
		3,
		30,
		Map(EnergyType.FIRE -> 2))),
	energyType = EnergyType.FIRE,
	weakness = Some(EnergyType.WATER),
	retreatCost = 0)