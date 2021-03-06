package src.card.pokemon.base_set

import src.board.intermediary.IntermediaryRequest
import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.card.condition.StatusCondition
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Gloom extends StageOnePokemon(
	"Gloom",
	"Gloom-Jungle-37.jpg",
	Deck.JUNGLE,
	Identifier.GLOOM,
	id = 44,
	maxHp = 60,
	firstMove = Some(new Move(
		"Poisonpowder",
        1,
        Map(EnergyType.GRASS -> 1)) {
			def perform = (owner, opp, args) => poisonAttack(owner, opp)
        }),
	secondMove = Some(new Move(
		"Foul Odor",
        2,
        Map(EnergyType.GRASS -> 2)) {
			def perform = (owner, opp, args) => {
				confuseAttack(owner, opp, 20)
				owner.active.get.inflictStatus(StatusCondition.CONFUSED)
			}
        }),
	energyType = EnergyType.GRASS,
	weakness = Some(EnergyType.FIRE),
	retreatCost = 1)