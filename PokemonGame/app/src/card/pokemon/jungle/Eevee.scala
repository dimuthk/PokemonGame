package src.card.pokemon.jungle

import src.json.Identifier
import src.move._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Eevee extends BasicPokemon(
	"Eevee",
	"Eevee-Jungle-51.jpg",
	Deck.JUNGLE,
	Identifier.EEVEE,
	id = 133,
	maxHp = 50,
	firstMove = Some(new Withdraw(
		"Tail Wag",
		1)),
	secondMove = Some(new Move(
		"Quick Attack",
        2) {
			def perform = (owner, opp, args) => flippedHeads() match {
				case true => standardAttack(owner, opp, 10)
				case false => standardAttack(owner, opp, 30)
			}
        }),
	energyType = EnergyType.COLORLESS,
	weakness = Some(EnergyType.FIGHTING),
	resistance = Some(EnergyType.PSYCHIC),
	retreatCost = 1)