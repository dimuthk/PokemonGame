package src.card.pokemon.base_set

import src.json.Identifier
import src.move._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Fearow extends StageOnePokemon(
	"Fearow",
	"Fearow-Jungle-36.jpg",
	Deck.JUNGLE,
	Identifier.FEAROW,
	id = 22,
	maxHp = 70,
	firstMove = Some(new Agility(
		"Agility",
		3,
		20)),
	secondMove = Some(new Move(
		"Drill Peck",
        4) {
			def perform = (owner, opp, args) => standardAttack(owner, opp, 40)
        }),
	energyType = EnergyType.COLORLESS,
	weakness = Some(EnergyType.THUNDER),
	resistance = Some(EnergyType.FIGHTING),
	retreatCost = 0)