package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Mewtwo extends BasicPokemon(
	"Mewtwo",
	"Mewtwo-Base-Set-10.jpg",
	Deck.BASE_SET,
	Identifier.MEWTWO,
	id = 150,
	maxHp = 60,
	firstMove = Some(new Move(
		"Psychic",
		1,
		Map(EnergyType.PSYCHIC -> 1)) {
			def perform = (owner, opp, args) => {
				val numECards = opp.active.get.energyCards.length
				standardAttack(owner, opp, 10 + numECards)
			}
		}),
	// TODO barrier
	energyType = EnergyType.PSYCHIC,
	weakness = Some(EnergyType.PSYCHIC),
	retreatCost = 3)