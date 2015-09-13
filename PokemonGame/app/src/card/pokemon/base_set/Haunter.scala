package src.card.pokemon.base_set

import src.card.condition.StatusCondition
import src.json.Identifier
import src.move._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Haunter extends StageOnePokemon(
	"Haunter",
	"Haunter-Base-Set-29.jpg",
	Deck.BASE_SET,
	Identifier.HAUNTER,
	id = 93,
	maxHp = 60,
	firstMove = Some(new Move(
		"Hypnosis",
		1,
		Map(EnergyType.PSYCHIC -> 1)) {
			def perform = (owner, opp, args) => sleepAttack(owner, opp)
		}),
	secondMove = Some(new Move(
		"Dream Eater",
		2,
		Map(EnergyType.PSYCHIC -> 2)) {
			def perform = (owner, opp, args) => opp.active.get.statusCondition match {
				case Some(StatusCondition.ASLEEP) => standardAttack(owner, opp, 50)
				case _ => throw new Exception("Shouldn't happen")
			}

			override def update = (owner, opp, pc, turnSwapped, isActive) => {
				super.update(owner, opp, pc, turnSwapped, isActive)
				opp.active.get.statusCondition match {
					case Some(StatusCondition.ASLEEP) => ()
					case _ => status = Status.DISABLED
				}
			}
		}),
	energyType = EnergyType.PSYCHIC,
	resistance = Some(EnergyType.FIGHTING),
	retreatCost = 1) {

	// TODO: do the same for status conditions

}