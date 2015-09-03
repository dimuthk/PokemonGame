package src.card.pokemon.fossil

import src.json.Identifier
import src.move._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

import play.api.Logger

class Krabby extends BasicPokemon(
	"Krabby",
	"Krabby-Fossil-51.jpg",
	Deck.FOSSIL,
	Identifier.KRABBY,
	id = 98,
	maxHp = 50,
	firstMove = Some(new CallForFamily(
      "Call for Family",
      selector = (card) => card match {
        case p : PokemonCard => p.id == 98
        case _ => false
      },
      totalEnergyReq = 1,
      Map(EnergyType.WATER -> 1))),
	secondMove = Some(new Move(
		"Irongrip",
		2,
		Map(EnergyType.WATER -> 1)) {
			def perform = (owner, opp, args) => standardAttack(owner, opp, 20)
	}),
	energyType = EnergyType.WATER,
	weakness = Some(EnergyType.THUNDER),
	retreatCost = 2)