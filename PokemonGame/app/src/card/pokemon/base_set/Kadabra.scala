package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Kadabra extends StageOnePokemon(
	"Kadabra",
	"Kadabra-Base-Set-32.jpg",
	Deck.BASE_SET,
	Identifier.KADABRA,
	id = 64,
	maxHp = 60,
	firstMove = Some(new Move(
		"Recover",
		2,
		Map(EnergyType.PSYCHIC -> 2)) {
			def perform = (owner, opp, args) => discardAndRecover(owner, EnergyType.PSYCHIC)
		}),
	secondMove = Some(new Move(
		"Super Psy",
		3,
		Map(EnergyType.PSYCHIC -> 2)) {
			def perform = (owner, opp, args) => standardAttack(owner, opp, 50)
		}),
	energyType = EnergyType.PSYCHIC,
	weakness = Some(EnergyType.PSYCHIC),
	retreatCost = 3)