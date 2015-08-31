package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Parasect extends StageOnePokemon(
	"Parasect",
	"Parasect-Jungle-59.jpg",
	Deck.JUNGLE,
	Identifier.PARASECT,
	id = 47,
	maxHp = 60,
	firstMove = Some(new Move(
		"Spore",
        2,
        Map(EnergyType.GRASS -> 2)) {
			def perform = (owner, opp, args) => sleepAttack(owner, opp)
        }),
	secondMove = Some(new Move(
		"Slash",
        3) {
			def perform = (owner, opp, args) => standardAttack(owner, opp, 30)
        }),
	energyType = EnergyType.GRASS,
	weakness = Some(EnergyType.FIRE),
	retreatCost = 1)