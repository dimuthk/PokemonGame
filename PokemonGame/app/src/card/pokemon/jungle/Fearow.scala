package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.card.pokemon.MirrorMoveable._
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
	firstMove = Some(new Move(
		"Peck",
		1) {
			def perform = (owner, opp) => standardAttack(owner, opp, 10)
		}),
	secondMove = Some(new MirrorMove(
        3) {}),
	energyType = EnergyType.COLORLESS,
	weakness = Some(EnergyType.THUNDER),
	resistance = Some(EnergyType.FIGHTING),
	retreatCost = 0) with MirrorMoveable