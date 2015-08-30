package src.card.pokemon.base_set

import src.json.Identifier
import src.move._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Oddish extends BasicPokemon(
	"Oddish",
	"Oddish-Jungle-58.jpg",
	Deck.JUNGLE,
	Identifier.ODDISH,
	id = 43,
	maxHp = 50,
	firstMove = Some(new Move(
		"Stun Spore",
        1,
        Map(EnergyType.GRASS -> 1)) {
			def perform = (owner, opp) => paralyzeChanceAttack(owner, opp, 10)
        }),
	secondMove = Some(new CallForFamily(
      "Sprout",
      moveNum = 2,
      selector = (card) => card match {
        case p : PokemonCard => p.id == 43
        case _ => false
      },
      totalEnergyReq = 2,
      Map(EnergyType.GRASS -> 2)) {}),
	energyType = EnergyType.GRASS,
	weakness = Some(EnergyType.FIRE),
	retreatCost = 1)