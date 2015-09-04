package src.card.pokemon.fossil

import src.json.Identifier
import src.move._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

import play.api.Logger

class Omanyte extends StageOnePokemon(
	"Omanyte",
	"Omanyte-Fossil-52.jpg",
	Deck.FOSSIL,
	Identifier.OMANYTE,
	id = 138,
	maxHp = 40,
	firstMove = Some(new PassivePokemonPower("Clairvoyance") {}),
	secondMove = Some(new Move(
      "Water Gun",
      1,
      Map(EnergyType.WATER -> 1)) {
        def perform = (owner, opp, args) => standardAttackPlusExtra(owner, opp, 10, EnergyType.WATER, 1)
      }),
	energyType = EnergyType.WATER,
	weakness = Some(EnergyType.GRASS),
	retreatCost = 1)