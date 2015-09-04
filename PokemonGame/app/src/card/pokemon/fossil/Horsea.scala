package src.card.pokemon.fossil

import src.json.Identifier
import src.move._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

import play.api.Logger

class Horsea extends StageOnePokemon(
	"Horsea",
	"Horsea-Fossil-49.jpg",
	Deck.FOSSIL,
	Identifier.HORSEA,
	id = 116,
	maxHp = 40,
	firstMove = Some(new Smokescreen(
      "Smokescreen",
      dmg = 10,
      1,
      Map(EnergyType.WATER -> 1))),
	energyType = EnergyType.WATER,
	weakness = Some(EnergyType.THUNDER),
	retreatCost = 0)