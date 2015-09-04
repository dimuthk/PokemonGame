package src.card.pokemon.jungle

import src.json.Identifier
import src.move._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

import play.api.Logger

class Rhyhorn extends BasicPokemon(
	"Rhyhorn",
	"Rhyhorn-Jungle-61.jpg",
	Deck.JUNGLE,
	Identifier.RHYHORN,
	id = 111,
	maxHp = 70,
	firstMove = Some(new Withdraw(
		"Leer",
		1)),
	secondMove = Some(new Move(
		"Horn Attack",
		3,
		Map(EnergyType.FIGHTING -> 1)) {
			def perform = (owner, opp, args) => standardAttack(owner, opp, 30)
		}),
	energyType = EnergyType.FIGHTING,
	weakness = Some(EnergyType.GRASS),
	resistance = Some(EnergyType.THUNDER),
	retreatCost = 3)