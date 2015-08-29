package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Rattata extends BasicPokemon(
	"Rattata",
	"Rattata-Base-Set-61.jpg",
	Deck.BASE_SET,
	Identifier.RATTATA,
	id = 19,
	maxHp = 30,
	firstMove = Some(new Move(
		"Bite",
		1) {
			def perform = (owner, opp) => standardAttack(owner, opp, 20)
		}),
	energyType = EnergyType.COLORLESS,
	weakness = Some(EnergyType.FIGHTING),
	resistance = Some(EnergyType.PSYCHIC),
	retreatCost = 0)