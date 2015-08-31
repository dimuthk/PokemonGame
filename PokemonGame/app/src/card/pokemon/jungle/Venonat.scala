package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Venonat extends BasicPokemon(
	"Venonat",
	"Venonat-Jungle-63.jpg",
	Deck.JUNGLE,
	Identifier.VENONAT,
	id = 48,
	maxHp = 40,
	firstMove = Some(new Move(
		"Stun Spore",
        1,
        Map(EnergyType.GRASS -> 1)) {
			def perform = (owner, opp, args) => paralyzeAttackChance(owner, opp, 10)
        }),
	secondMove = Some(new Move(
		"Leech Life",
        2,
        Map(EnergyType.GRASS -> 1)) {
			def perform = (owner, opp, args) => healthDrainAttack(owner, opp, 10)
        }),
	energyType = EnergyType.GRASS,
	weakness = Some(EnergyType.FIRE),
	retreatCost = 1)