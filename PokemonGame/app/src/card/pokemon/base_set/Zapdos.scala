package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Zapdos extends BasicPokemon(
	"Zapdos",
	"Zapdos-Base-Set-16.jpg",
	Deck.BASE_SET,
	Identifier.ZAPDOS,
	id = 145,
	maxHp = 90,
	firstMove = Some(new Move(
		"Thunder",
		4,
		Map(EnergyType.THUNDER -> 3)) {
			def perform = (owner, opp, args) => selfDamageChanceAttack(owner, opp, 60, 30)
		}),
	secondMove = Some(new Move(
		"Thunderbolt",
		4,
		Map(EnergyType.THUNDER -> 4)) {
			def perform = (owner, opp, args) => {
				val active = owner.active.get
				owner.discardEnergyFromCard(active, cnt = active.energyCards.length)
				standardAttack(owner, opp, 100)
			}
		}),
	energyType = EnergyType.THUNDER,
	resistance = Some(EnergyType.FIGHTING),
	retreatCost = 3)