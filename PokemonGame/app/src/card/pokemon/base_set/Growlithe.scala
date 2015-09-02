package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Growlithe extends BasicPokemon(
	"Growlithe",
	"Growlithe-Base-Set-28.jpg",
	Deck.BASE_SET,
	Identifier.GROWLITHE,
	id = 58,
	maxHp = 60,
	firstMove = Some(new Move(
		"Flare",
		2,
		Map(EnergyType.FIRE -> 1)) {
			def perform = (owner, opp, args) => standardAttack(owner, opp, 20)
		}),
	energyType = EnergyType.FIRE,
	weakness = Some(EnergyType.WATER),
	retreatCost = 1)