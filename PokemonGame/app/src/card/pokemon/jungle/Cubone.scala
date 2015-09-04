package src.card.pokemon.jungle

import src.json.Identifier
import src.move._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Cubone extends BasicPokemon(
	"Cubone",
	"Cubone-Jungle-50.jpg",
	Deck.JUNGLE,
	Identifier.CUBONE,
	id = 104,
	maxHp = 40,
	firstMove = Some(new Move(
      "Minimize",
      1) {
        def perform = (owner, opp, args) => minimize(owner)
      }),
	secondMove = Some(new Move(
		"Rage",
        2,
        Map(EnergyType.FIGHTING -> 2)) {
			def perform = (owner, opp, args) => {
				val dmg = 10 + 10 * (owner.active.get.maxHp - owner.active.get.currHp)
				standardAttack(owner, opp, dmg)
			}
        }),
	energyType = EnergyType.FIGHTING,
	weakness = Some(EnergyType.GRASS),
	resistance = Some(EnergyType.THUNDER),
	retreatCost = 1)