package src.card.pokemon.fossil

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class MagnetonFossil extends StageOnePokemon(
	"Magneton",
	"Magneton-Fossil-11.jpg",
	Deck.FOSSIL,
	Identifier.MAGNETON_FOSSIL,
	id = 82,
	maxHp = 80,
	firstMove = Some(new Move(
		"Thunder Wave",
		3,
		Map(EnergyType.THUNDER -> 2)) {
			def perform = (owner, opp, args) => ignoreTypesAttack(owner, opp, 20)
		}),
	secondMove = Some(new Move(
		"Selfdestruct",
		4,
		Map(EnergyType.THUNDER -> 2)) {
			def perform = (owner, opp, args) => selfDestruct(owner, opp, 100, 100, 20)
		}),
	energyType = EnergyType.THUNDER,
	weakness = Some(EnergyType.FIGHTING),
	retreatCost = 2)