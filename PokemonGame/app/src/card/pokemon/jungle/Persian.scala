package src.card.pokemon.base_set

import src.json.Identifier
import src.move._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Persian extends StageOnePokemon(
	"Persian",
	"Persian-Jungle-42.jpg",
	Deck.JUNGLE,
	Identifier.PERSIAN,
	id = 53,
	maxHp = 70,
	firstMove = Some(new Move(
		"Scratch",
        2) {
			def perform = (owner, opp, args) => standardAttack(owner, opp, 20)
        }),
	secondMove = Some(new Move(
		"Pounce",
		3) {
			def perform = (owner, opp, args) => {
				owner.active.get.pounced = true
				standardAttack(owner, opp, 30)
			}
		}),
	energyType = EnergyType.COLORLESS,
	weakness = Some(EnergyType.FIGHTING),
	resistance = Some(EnergyType.PSYCHIC),
	retreatCost = 0)