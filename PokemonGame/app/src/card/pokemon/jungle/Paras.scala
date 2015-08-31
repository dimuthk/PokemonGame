package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Paras extends BasicPokemon(
	"Paras",
	"Paras-Jungle-59.jpg",
	Deck.JUNGLE,
	Identifier.PARAS,
	id = 46,
	maxHp = 40,
	firstMove = Some(new Move(
		"Scratch",
        2) {
			def perform = (owner, opp, args) => standardAttack(owner, opp, 20)
        }),
	secondMove = Some(new Move(
		"Spore",
        2,
        Map(EnergyType.GRASS -> 2)) {
			def perform = (owner, opp, args) => sleepAttack(owner, opp)
        }),
	energyType = EnergyType.GRASS,
	weakness = Some(EnergyType.FIRE),
	retreatCost = 1)