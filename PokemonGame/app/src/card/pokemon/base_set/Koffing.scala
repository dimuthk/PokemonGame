package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Koffing extends BasicPokemon(
	"Koffing",
	"Koffing-Base-Set-51.jpg",
	Deck.BASE_SET,
	Identifier.KOFFING,
	id = 109,
	maxHp = 50,
	firstMove = Some(new Move(
		"Foul Gas",
		2,
		Map(EnergyType.GRASS -> 2)) {
			def perform = (owner, opp, args) => flippedHeads() match {
				case true => poisonAttack(owner, opp, 10)
				case false => confuseAttack(owner, opp, 10)
			}
		}),
	energyType = EnergyType.GRASS,
	weakness = Some(EnergyType.PSYCHIC),
	retreatCost = 1)