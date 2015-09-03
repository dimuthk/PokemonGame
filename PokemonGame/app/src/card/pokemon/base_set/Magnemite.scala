package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Magnemite extends BasicPokemon(
	"Magnemite",
	"Magnemite-Base-Set-53.jpg",
	Deck.BASE_SET,
	Identifier.MAGNEMITE,
	id = 81,
	maxHp = 40,
	firstMove = Some(new Move(
		"Thunder Wave",
		1,
		Map(EnergyType.THUNDER -> 1)) {
			def perform = (owner, opp, args) => paralyzeAttackChance(owner, opp, 10)
		}),
	secondMove = Some(new Move(
		"Selfdestruct",
		2,
		Map(EnergyType.THUNDER -> 1)) {
			def perform = (owner, opp, args) => selfDestruct(owner, opp, 40, 40, 10)
		}),
	energyType = EnergyType.THUNDER,
	weakness = Some(EnergyType.FIGHTING),
	retreatCost = 1)