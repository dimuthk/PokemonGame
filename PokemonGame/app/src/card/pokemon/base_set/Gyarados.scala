package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Gyarados extends StageOnePokemon(
	"Gyarados",
	"Gyarados-Base-Set-6.jpg",
	Deck.BASE_SET,
	Identifier.GYARADOS,
	id = 130,
	maxHp = 100,
	firstMove = Some(new Move(
		"Dragon Rage",
		3,
		Map(EnergyType.WATER -> 3)) {
			def perform = (owner, opp, args) => standardAttack(owner, opp, 50)
		}),
	secondMove = Some(new Move(
		"Bubblebeam",
		4,
		Map(EnergyType.WATER -> 4)) {
			def perform = (owner, opp, args) => paralyzeAttackChance(owner, opp, 40)
		}),
	energyType = EnergyType.WATER,
	weakness = Some(EnergyType.THUNDER),
	retreatCost = 3)