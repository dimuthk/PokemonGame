package src.card.pokemon.base_set

import src.json.Identifier
import src.move._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Dragonair extends StageOnePokemon(
	"Dragonair",
	"Dragonair-Base-Set-18.jpg",
	Deck.BASE_SET,
	Identifier.DRAGONAIR,
	id = 148,
	maxHp = 80,
	firstMove = Some(new Move(
		"Slam",
		3) {
			def perform = (owner, opp, args) => multipleHitAttack(owner, opp, 30, 2)
		}),
	secondMove = Some(new HyperBeam(
      "Hyper Beam",
      dmg = 20,
      4)),
	energyType = EnergyType.COLORLESS,
	resistance = Some(EnergyType.PSYCHIC),
	retreatCost = 2)