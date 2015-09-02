package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Machoke extends StageOnePokemon(
	"Machop",
	"Machop-Base-Set-34.jpg",
	Deck.BASE_SET,
	Identifier.MACHOKE,
	id = 67,
	maxHp = 80,
	firstMove = Some(new Move(
		"Karate Chop",
		3,
		Map(EnergyType.FIGHTING -> 2)) {
			def perform = (owner, opp, args) => standardAttack(owner, opp, 50 - (owner.active.get.maxHp - owner.active.get.currHp))
		}),
	secondMove = Some(new Move(
		"Submission",
		4,
		Map(EnergyType.FIGHTING -> 2)) {
			def perform = (owner, opp, args) => attackAndHurtSelf(owner, opp, 60, 20)
		}),
	energyType = EnergyType.FIGHTING,
	weakness = Some(EnergyType.PSYCHIC),
	retreatCost = 3)