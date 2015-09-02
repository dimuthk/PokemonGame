package src.card.pokemon.base_set

import src.json.Identifier
import src.move._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Bellsprout extends BasicPokemon(
	"Bellsprout",
	"Bellsprout-Jungle-49.jpg",
	Deck.JUNGLE,
	Identifier.BELLSPROUT,
	id = 69,
	maxHp = 40,
	firstMove = Some(new Move(
		"Vine Whip",
        1,
        Map(EnergyType.GRASS -> 1)) {
			def perform = (owner, opp, args) => standardAttack(owner, opp, 10)
        }),
	secondMove = Some(new CallForFamily(
      "Call For Family",
      selector = (card) => card match {
        case p : PokemonCard => p.id == 69
        case _ => false
      },
      totalEnergyReq = 1,
      Map(EnergyType.GRASS -> 1))),
	energyType = EnergyType.GRASS,
	weakness = Some(EnergyType.FIRE),
	retreatCost = 1)