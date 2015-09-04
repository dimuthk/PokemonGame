package src.card.pokemon.jungle

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Pinsir extends BasicPokemon(
	"Pinsir",
	"Pinsir-Jungle-9.jpg",
	Deck.JUNGLE,
	Identifier.PINSIR,
	id = 127,
	maxHp = 60,
	firstMove = Some(new Move(
		"Irongrip",
        2,
        Map(EnergyType.GRASS -> 2)) {
			def perform = (owner, opp, args) => paralyzeAttackChance(owner, opp, 20)
        }),
	secondMove = Some(new Move(
		"Guillotine",
        4,
        Map(EnergyType.GRASS -> 2)) {
			def perform = (owner, opp, args) => standardAttack(owner, opp, 50)
        }),
	energyType = EnergyType.GRASS,
	weakness = Some(EnergyType.FIRE),
	retreatCost = 1)