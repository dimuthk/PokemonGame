package src.card.pokemon.jungle

import src.json.Identifier
import src.move._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Scyther extends BasicPokemon(
	"Scyther",
	"Scyther-Jungle-10.jpg",
	Deck.JUNGLE,
	Identifier.SCYTHER,
	id = 123,
	maxHp = 70,
	firstMove = Some(new Move(
		"Swords Dance",
        1,
        Map(EnergyType.GRASS -> 1)) {
			def perform = (owner, opp, args) => {
				owner.active.get.swordsDance = true
				None
			}
        }),
	secondMove = Some(new Move(
		"Slash",
		3) {
			def perform = (owner, opp, args) => owner.active.get.swordsDance match {
				case true => standardAttack(owner, opp, 60)
				case false => standardAttack(owner, opp, 30)
			}
		}),
	energyType = EnergyType.GRASS,
	weakness = Some(EnergyType.FIRE),
	resistance = Some(EnergyType.FIGHTING),
	retreatCost = 0)