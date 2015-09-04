package src.card.pokemon.jungle

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Jolteon extends StageOnePokemon(
	"Jolteon",
	"Jolteon-Jungle-4.jpg",
	Deck.JUNGLE,
	Identifier.JOLTEON,
	id = 135,
	maxHp = 70,
	firstMove = Some(new Move(
		"Quick Attack",
        2) {
			def perform = (owner, opp, args) => flippedHeads() match {
				case true => standardAttack(owner, opp, 10)
				case false => standardAttack(owner, opp, 30)
			}
        }),
	secondMove = Some(new Move(
      "Pin Missile",
      3,
      Map(EnergyType.THUNDER -> 2)) {
        def perform = (owner, opp, args) => multipleHitAttack(owner, opp, 20, 4)
      }),
	energyType = EnergyType.THUNDER,
	weakness = Some(EnergyType.FIGHTING),
	retreatCost = 1)