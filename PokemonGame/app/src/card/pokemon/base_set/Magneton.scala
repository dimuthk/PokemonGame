package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Magneton extends StageOnePokemon(
	"Magneton",
	"Magneton-Base-Set-9.jpg",
	Deck.BASE_SET,
	Identifier.MAGNETON,
	id = 82,
	maxHp = 60,
	firstMove = Some(new Move(
		"Thunder Wave",
		3,
		Map(EnergyType.THUNDER -> 2)) {
			def perform = (owner, opp, args) => paralyzeAttackChance(owner, opp, 30)
		}),
	secondMove = Some(new Move(
		"Selfdestruct",
		4,
		Map(EnergyType.THUNDER -> 2)) {
			def perform = (owner, opp, args) => selfDestruct(owner, opp, 80, 80, 20)
		}),
	energyType = EnergyType.THUNDER,
	weakness = Some(EnergyType.FIGHTING),
	retreatCost = 1)