package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Starmie extends StageOnePokemon(
	"Starmie",
	"Starmie-Base-Set-64.jpg",
	Deck.BASE_SET,
	Identifier.STARMIE,
	id = 121,
	maxHp = 60,
	firstMove = Some(new Move(
		"Recover",
		2,
		Map(EnergyType.WATER -> 2)) {
			def perform = (owner, opp, args) => discardAndRecover(owner, EnergyType.WATER)
		}),
	secondMove = Some(new Move(
		"Star Freeze",
		3,
		Map(EnergyType.WATER -> 1)) {
			def perform = (owner, opp, args) => paralyzeAttackChance(owner, opp, 20)
		}),
	energyType = EnergyType.WATER,
	weakness = Some(EnergyType.THUNDER),
	retreatCost = 1)