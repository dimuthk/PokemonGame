package src.card.pokemon.jungle

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Vaporeon extends StageOnePokemon(
	"Vaporeon",
	"Vaporeon-Jungle-12.jpg",
	Deck.JUNGLE,
	Identifier.VAPOREON,
	id = 134,
	maxHp = 80,
	firstMove = Some(new Move(
		"Quick Attack",
        2) {
			def perform = (owner, opp, args) => flippedHeads() match {
				case true => standardAttack(owner, opp, 10)
				case false => standardAttack(owner, opp, 30)
			}
        }),
	secondMove = Some(new Move(
      "Water Gun",
      3,
      Map(EnergyType.WATER -> 2)) {
        def perform = (owner, opp, args) => standardAttackPlusExtra(owner, opp, 30, EnergyType.WATER, 3)
      }),
	energyType = EnergyType.WATER,
	weakness = Some(EnergyType.THUNDER),
	retreatCost = 1)