package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Goldeen extends BasicPokemon(
	"Goldeen",
	"Goldeen-Jungle-53.jpg",
	Deck.JUNGLE,
	Identifier.GOLDEEN,
	id = 118,
	maxHp = 40,
	firstMove = Some(new Move(
		"Horn Attack",
        1,
        Map(EnergyType.WATER -> 1)) {
			def perform = (owner, opp, args) => standardAttack(owner, opp, 10)
        }),
	energyType = EnergyType.WATER,
	weakness = Some(EnergyType.THUNDER),
	retreatCost = 0)