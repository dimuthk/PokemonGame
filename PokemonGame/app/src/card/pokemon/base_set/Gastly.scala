package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Gastly extends BasicPokemon(
	"Gastly",
	"Gastly-Base-Set-50.jpg",
	Deck.BASE_SET,
	Identifier.GASTLY,
	id = 92,
	maxHp = 30,
	firstMove = Some(new Move(
		"Sleeping Gas",
		1,
		Map(EnergyType.PSYCHIC -> 1)) {
			def perform = (owner, opp, args) => sleepAttackChance(owner, opp)
		}),
	secondMove = Some(new Move(
		"Destiny Bond",
		2,
		Map(EnergyType.PSYCHIC -> 1)) {
			def perform = (owner, opp, args) => {
				owner.discardEnergyFromCard(owner.active.get, eType = EnergyType.PSYCHIC)
    			owner.active.get.destinyBond = true
			}
		}),
	energyType = EnergyType.PSYCHIC,
	resistance = Some(EnergyType.FIGHTING),
	retreatCost = 0)