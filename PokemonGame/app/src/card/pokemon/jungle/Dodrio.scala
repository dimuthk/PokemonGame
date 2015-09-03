package src.card.pokemon.jungle

import src.json.Identifier
import src.move._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Dodrio extends BasicPokemon(
	"Dodrio",
	"Dodrio-Jungle-34.jpg",
	Deck.JUNGLE,
	Identifier.DODRIO,
	id = 85,
	maxHp = 70,
	firstMove = Some(new PassivePokemonPower("Retreat Aid") {}),
	secondMove = Some(new Move(
		"Rage",
        3) {
			def perform = (owner, opp, args) => {
				val dmg = 10 + 10 * (owner.active.get.maxHp - owner.active.get.currHp)
				standardAttack(owner, opp, dmg)
			}
        }),
	energyType = EnergyType.COLORLESS,
	weakness = Some(EnergyType.THUNDER),
	resistance = Some(EnergyType.FIGHTING),
	retreatCost = 0)