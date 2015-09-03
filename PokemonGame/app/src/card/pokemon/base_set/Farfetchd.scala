package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Farfetchd extends BasicPokemon(
	"Farfetch'd",
	"Farfetch'd-Base-Set-27.jpg",
	Deck.BASE_SET,
	Identifier.FARFETCHD,
	id = 83,
	maxHp = 50,
	firstMove = Some(new Move(
		"Leek Slap",
		1) {
			var used = false
			def perform = (owner, opp, args) => (used, flippedHeads()) match {
				case (false, true) => {
					used = true
					standardAttack(owner, opp, 30)
				}
				case _ => {
					used = true
					None
				}
			}
		}),
	secondMove = Some(new Move(
		"Pot Smash",
		3) {
			def perform = (owner, opp, args) => standardAttack(owner, opp, 30)
		}),
	energyType = EnergyType.COLORLESS,
	weakness = Some(EnergyType.THUNDER),
	resistance = Some(EnergyType.FIGHTING),
	retreatCost = 1)