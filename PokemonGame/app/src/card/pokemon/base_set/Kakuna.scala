package src.card.pokemon.base_set

import src.json.Identifier
import src.move._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Kakuna extends StageOnePokemon(
	"Kakuna",
	"Kakuna-Base-Set-33.jpg",
	Deck.BASE_SET,
	Identifier.KAKUNA,
	id = 14,
	maxHp = 80,
	firstMove = Some(new Withdraw(
		"Stiffen",
		2)),
	energyType = EnergyType.GRASS,
	weakness = Some(EnergyType.FIRE),
	retreatCost = 2)