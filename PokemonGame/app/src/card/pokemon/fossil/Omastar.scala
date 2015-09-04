package src.card.pokemon.fossil

import src.json.Identifier
import src.move._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

import play.api.Logger

class Omastar extends StageTwoPokemon(
	"Omastar",
	"Omastar-Fossil-40.jpg",
	Deck.FOSSIL,
	Identifier.OMASTAR,
	id = 139,
	maxHp = 70,
	firstMove = Some(new Move(
      "Water Gun",
      2,
      Map(EnergyType.WATER -> 1)) {
        def perform = (owner, opp, args) => standardAttackPlusExtra(owner, opp, 20, EnergyType.WATER, 2)
      }),
	secondMove = Some(new Move(
      "Spike Cannon",
      2,
      Map(EnergyType.WATER -> 2)) {
        def perform = (owner, opp, args) => multipleHitAttack(owner, opp, 30, 2)
      }),
	energyType = EnergyType.WATER,
	weakness = Some(EnergyType.GRASS),
	retreatCost = 1)