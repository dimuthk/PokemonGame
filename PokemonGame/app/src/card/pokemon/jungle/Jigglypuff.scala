package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Jigglypuff extends BasicPokemon(
	"Jigglypuff",
	"Jigglypuff-Jungle-54.jpg",
	Deck.JUNGLE,
	Identifier.JIGGLYPUFF,
	id = 39,
	maxHp = 60,
	firstMove = Some(new Move(
		"Lullaby",
        1) {
			def perform = (owner, opp) => sleepAttack(owner, opp)
        }),
	secondMove = Some(new Move(
		"Pound",
        2) {
			def perform = (owner, opp) => standardAttack(owner, opp, 20)
        }),
	energyType = EnergyType.COLORLESS,
	weakness = Some(EnergyType.FIGHTING),
	resistance = Some(EnergyType.PSYCHIC),
	retreatCost = 1)