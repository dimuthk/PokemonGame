package src.card.pokemon.fossil

import src.json.Identifier
import src.move._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

import play.api.Logger

class Lapras extends BasicPokemon(
	"Lapras",
	"Lapras-Fossil-10.jpg",
	Deck.FOSSIL,
	Identifier.LAPRAS,
	id = 131,
	maxHp = 80,
	firstMove = Some(new Move(
      "Water Gun",
      1,
      Map(EnergyType.WATER -> 1)) {
        def perform = (owner, opp, args) => standardAttackPlusExtra(owner, opp, 10, EnergyType.WATER, 1)
      }),
	secondMove = Some(new Move(
		"Confuse Ray",
		2,
		Map(EnergyType.WATER -> 2)) {
			def perform = (owner, opp, args) => confuseAttackChance(owner, opp, 10)
	}),
	energyType = EnergyType.WATER,
	weakness = Some(EnergyType.THUNDER),
	retreatCost = 2)