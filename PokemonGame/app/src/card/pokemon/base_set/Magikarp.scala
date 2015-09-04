package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Magikarp extends BasicPokemon(
	"Magikarp",
	"Magikarp-Base-Set-35.jpg",
	Deck.BASE_SET,
	Identifier.MAGIKARP,
	id = 129,
	maxHp = 30,
	firstMove = Some(new Move(
		"Tackle",
		1) {
			def perform = (owner, opp, args) => standardAttack(owner, opp, 10)
		}),
	secondMove = Some(new Move(
		"Flail",
		1,
		Map(EnergyType.WATER -> 1)) {
			def perform = (owner, opp, args) => {
				val dmg = owner.active.get.maxHp - owner.active.get.currHp
				standardAttack(owner, opp, dmg)
			}
		}),
	energyType = EnergyType.WATER,
	weakness = Some(EnergyType.THUNDER),
	retreatCost = 1)