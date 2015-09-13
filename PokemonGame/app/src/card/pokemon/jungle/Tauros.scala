package src.card.pokemon.jungle

import src.json.Identifier
import src.card.condition._
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Tauros extends BasicPokemon(
	"Tauros",
	"Tauros-Jungle-47.jpg",
	Deck.JUNGLE,
	Identifier.TAUROS,
	id = 128,
	maxHp = 60,
	firstMove = Some(new Move(
		"Stomp",
        2) {
			def perform = (owner, opp, args) => flippedHeads() match {
				case true => standardAttack(owner, opp, 30)
				case false => standardAttack(owner, opp, 20)
			}
        }),
	secondMove = Some(new Move(
		"Rampage",
        3) {
			def perform = (owner, opp, args) => {
				val dmg = 20 + owner.active.get.maxHp - owner.active.get.currHp
				standardAttack(owner, opp, dmg)
				if (!flippedHeads()) {
					owner.active.get.inflictStatus(StatusCondition.CONFUSED)
				}
			}
        }),
	energyType = EnergyType.COLORLESS,
	weakness = Some(EnergyType.FIGHTING),
	resistance = Some(EnergyType.PSYCHIC),
	retreatCost = 2)