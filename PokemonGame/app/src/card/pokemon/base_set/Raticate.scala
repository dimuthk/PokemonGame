package src.card.pokemon.base_set

import src.board.intermediary.IntermediaryRequest
import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Raticate extends StageOnePokemon(
	"Raticate",
	"Raticate-Base-Set-40.jpg",
	Deck.BASE_SET,
	Identifier.RATICATE,
	id = 20,
	maxHp = 60,
	firstMove = Some(new Move(
		"Bite",
		1) {
			def perform = (owner, opp, args) => standardAttack(owner, opp, 20)
		}),
	secondMove = Some(new Move(
		"Super Fang",
		3) {
			def perform = (owner, opp, args) => {
				val dmg = roundUp(opp.active.get.currHp / 2)
				opp.active.get.takeDamage(dmg)
				None
			}
		}),
	energyType = EnergyType.COLORLESS,
	weakness = Some(EnergyType.FIGHTING),
	resistance = Some(EnergyType.PSYCHIC),
	retreatCost = 1)