package src.card.pokemon.base_set

import src.json.Identifier
import src.move._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Weepinbell extends StageOnePokemon(
	"Weepinbell",
	"Weepinbell-Jungle-48.jpg",
	Deck.JUNGLE,
	Identifier.WEEPINBELL,
	id = 70,
	maxHp = 70,
	firstMove = Some(new Move(
		"Poisonpowder",
        1,
        Map(EnergyType.GRASS -> 1)) {
			def perform = (owner, opp, args) => poisonAttackChance(owner, opp, 10)
        }),
	secondMove = Some(new Move(
		"Razor Leaf",
		2,
		Map(EnergyType.GRASS -> 2)) {
			def perform = (owner, opp, args) => standardAttack(owner, opp, 30)
		}),
	energyType = EnergyType.GRASS,
	weakness = Some(EnergyType.FIRE),
	retreatCost = 1)