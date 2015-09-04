package src.card.pokemon.jungle

import src.json.Identifier
import src.move._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Kangaskhan extends BasicPokemon(
	"Kangaskhan",
	"Kangaskhan-Jungle-5.jpg",
	Deck.JUNGLE,
	Identifier.KANGASKHAN,
	id = 115,
	maxHp = 90,
	firstMove = Some(new Move(
      "Fetch",
      1) {
        def perform = (owner, opp, args) => {
        	owner.dealCardsToHand(1)
        	None
        }
      }),
	secondMove = Some(new Move(
		"Comet Punch",
        4) {
			def perform = (owner, opp, args) => multipleHitAttack(owner, opp, 20, 4)
        }),
	energyType = EnergyType.COLORLESS,
	weakness = Some(EnergyType.FIGHTING),
	resistance = Some(EnergyType.PSYCHIC),
	retreatCost = 3)