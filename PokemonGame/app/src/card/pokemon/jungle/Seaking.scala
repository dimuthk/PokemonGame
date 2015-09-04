package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Seaking extends StageOnePokemon(
	"Seaking",
	"Seaking-Jungle-46.jpg",
	Deck.JUNGLE,
	Identifier.SEAKING,
	id = 119,
	maxHp = 70,
	firstMove = Some(new Move(
		"Horn Attack",
        1,
        Map(EnergyType.WATER -> 1)) {
			def perform = (owner, opp, args) => standardAttack(owner, opp, 10)
        }),
	secondMove = Some(new Move(
		"Waterfall",
        2,
        Map(EnergyType.WATER -> 1)) {
			def perform = (owner, opp, args) => standardAttack(owner, opp, 30)
        }),
	energyType = EnergyType.WATER,
	weakness = Some(EnergyType.THUNDER),
	retreatCost = 1)