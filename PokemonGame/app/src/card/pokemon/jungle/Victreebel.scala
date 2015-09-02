package src.card.pokemon.base_set

import src.json.Identifier
import src.move._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Victreebel extends StageOnePokemon(
	"Victreebel",
	"Victreebel-Jungle-14.jpg",
	Deck.JUNGLE,
	Identifier.VICTREEBEL,
	id = 71,
	maxHp = 80,
	firstMove = Some(new Whirlwind(
        "Lure",
        ownerChooses = true,
        dmg = 0,
        totalEnergyReq = 1,
        Map(EnergyType.GRASS -> 1))),
	secondMove = Some(new Move(
		"Acid",
		2,
		Map(EnergyType.GRASS -> 2)) {
			def perform = (owner, opp, args) => {
				if (flippedHeads()) {
					opp.active.get.acid = true
				}
				standardAttack(owner, opp, 20)
			}
		}),
	energyType = EnergyType.GRASS,
	weakness = Some(EnergyType.FIRE),
	retreatCost = 2)