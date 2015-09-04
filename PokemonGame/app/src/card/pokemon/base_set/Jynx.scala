package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Jynx extends BasicPokemon(
	"Jynx",
	"Jynx-Base-Set-31.jpg",
	Deck.BASE_SET,
	Identifier.JYNX,
	id = 124,
	maxHp = 70,
	firstMove = Some(new Move(
		"Doubleslap",
		1,
		Map(EnergyType.PSYCHIC -> 1)) {
			def perform = (owner, opp, args) => multipleHitAttack(owner, opp, 10, 2)
		}),
	secondMove = Some(new Move(
		"Meditate",
		3,
		Map(EnergyType.PSYCHIC -> 2)) {
			def perform = (owner, opp, args) => standardAttack(owner, opp, 20 + 10 * (opp.active.get.maxHp - opp.active.get.currHp))
		}),
	energyType = EnergyType.PSYCHIC,
	weakness = Some(EnergyType.PSYCHIC),
	retreatCost = 0)