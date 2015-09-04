package src.card.pokemon.fossil

import src.json.Identifier
import src.move._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

import play.api.Logger

class Kabutops extends StageTwoPokemon(
	"Kabutops",
	"Kabutops-Fossil-9.jpg",
	Deck.FOSSIL,
	Identifier.KABUTOPS,
	id = 141,
	maxHp = 60,
	firstMove = Some(new Move(
		"Sharp Sickle",
		2,
		Map(EnergyType.FIGHTING -> 2)) {
			def perform = (owner, opp, args) => standardAttack(owner, opp, 30)
		}),
	secondMove = Some(new Move(
      "Absorb",
      4,
      Map(EnergyType.FIGHTING -> 4)) {
        def perform = (owner, opp, args) => {
          val dmg = opp.active.get.calculateDmg(owner.active.get, 40)
          owner.active.get.heal(roundUp(dmg / 2))
          standardAttack(owner, opp, 40)
        }
    }),
	energyType = EnergyType.FIGHTING,
	weakness = Some(EnergyType.GRASS),
	retreatCost = 1)