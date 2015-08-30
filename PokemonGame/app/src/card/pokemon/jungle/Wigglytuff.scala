package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Wigglytuff extends StageOnePokemon(
	"Wigglytuff",
	"Wigglytuff-Jungle-32.jpg",
	Deck.JUNGLE,
	Identifier.WIGGLYTUFF,
	id = 40,
	maxHp = 80,
	firstMove = Some(new Move(
		"Lullaby",
        1) {
			def perform = (owner, opp) => sleepAttack(owner, opp)
        }),
	secondMove = Some(new Move(
		"Do The Wave",
        3) {
			def perform = (owner, opp) => standardAttack(owner, opp, 10 + 10 * owner.bench.toList.flatten.length)
        }),
	energyType = EnergyType.COLORLESS,
	weakness = Some(EnergyType.FIGHTING),
	resistance = Some(EnergyType.PSYCHIC),
	retreatCost = 2)