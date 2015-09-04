package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Electabuzz extends BasicPokemon(
	"Electabuzz",
	"Electabuzz-Base-Set-20.jpg",
	Deck.BASE_SET,
	Identifier.ELECTABUZZ,
	id = 125,
	maxHp = 70,
	firstMove = Some(new Move(
		"Thundershock",
		1,
		Map(EnergyType.THUNDER -> 1)) {
			def perform = (owner, opp, args) => paralyzeAttackChance(owner, opp, 10)
		}),
	secondMove = Some(new Move(
		"Thunderpunch",
		2,
		Map(EnergyType.THUNDER -> 1)) {
			def perform = (owner, opp, args) => flippedHeads() match {
				case true => standardAttack(owner, opp, 40)
				case false => standardAttack(owner, opp, 30)
			}
		}),
	energyType = EnergyType.THUNDER,
	weakness = Some(EnergyType.FIGHTING),
	retreatCost = 2)