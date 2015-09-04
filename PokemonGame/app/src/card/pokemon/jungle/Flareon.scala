package src.card.pokemon.jungle

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Flareon extends StageOnePokemon(
	"Flareon",
	"Flareon-Jungle-3.jpg",
	Deck.JUNGLE,
	Identifier.FLAREON,
	id = 136,
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
      "Flamethrower",
      4,
      Map(EnergyType.FIRE -> 2)) {
        def perform = (owner, opp, args) => energyDiscardAttack(owner, opp, 60, EnergyType.FIRE)
      }),
	energyType = EnergyType.FIRE,
	weakness = Some(EnergyType.WATER),
	retreatCost = 1)