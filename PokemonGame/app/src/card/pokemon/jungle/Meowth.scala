package src.card.pokemon.base_set

import src.json.Identifier
import src.move._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Meowth extends BasicPokemon(
	"Meowth",
	"Meowth-Jungle-56.jpg",
	Deck.JUNGLE,
	Identifier.MEOWTH,
	id = 52,
	maxHp = 50,
	firstMove = Some(new Move(
		"Pay Day",
        2) {
			def perform = (owner, opp, args) => {
				if (flippedHeads()) {
					owner.dealCardsToHand(1)
				}
				standardAttack(owner, opp, 10)
			}
        }),
	energyType = EnergyType.COLORLESS,
	weakness = Some(EnergyType.FIGHTING),
	resistance = Some(EnergyType.PSYCHIC),
	retreatCost = 1)