package src.card.pokemon.fossil

import src.json.Identifier
import src.move._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

import play.api.Logger

class Seadra extends StageOnePokemon(
	"Seadra",
	"Seadra-Fossil-42.jpg",
	Deck.FOSSIL,
	Identifier.SEADRA,
	id = 117,
	maxHp = 60,
	firstMove = Some(new Move(
      "Water Gun",
      2,
      Map(EnergyType.WATER -> 1)) {
        def perform = (owner, opp, args) => standardAttackPlusExtra(owner, opp, 20, EnergyType.WATER, 3)
      }),
	secondMove = Some(new Agility(
		"Agility",
		3,
		20,
		Map(EnergyType.WATER -> 1))),
	energyType = EnergyType.WATER,
	weakness = Some(EnergyType.THUNDER),
	retreatCost = 1)