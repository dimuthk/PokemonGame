package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Charmander extends BasicPokemon(
	"Charmander",
	"Charmander-Base-Set-46.jpg",
	Deck.BASE_SET,
	Identifier.CHARMANDER,
	id = 4,
	maxHp = 50,
	firstMove = Some(new Move(
		"Scratch",
		1,
		Map()) {
			def perform = (owner, opp, args) => standardAttack(owner, opp, 10)
	}),
	secondMove = Some(new Move(
		"Ember",
		2,
		Map(EnergyType.FIRE -> 1)) {
			def perform = (owner, opp, args) => energyDiscardAttack(owner, opp, 30, EnergyType.FIRE)
	}),
	energyType = EnergyType.FIRE,
	weakness = Some(EnergyType.WATER),
	retreatCost = 1)