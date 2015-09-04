package src.card.pokemon.fossil

import src.json.Identifier
import src.move._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

import play.api.Logger

class Aerodactyl extends StageOnePokemon(
	"Aerodactyl",
	"Aerodactyl-Fossil-1.jpg",
	Deck.FOSSIL,
	Identifier.AERODACTYL,
	id = 142,
	maxHp = 60,
	firstMove = Some(new PassivePokemonPower("Prehistoric Power") {}),
	secondMove = Some(new Move(
      "Wing Attack",
      3) {
        def perform = (owner, opp, args) => standardAttack(owner, opp, 30)
      }),
	energyType = EnergyType.FIGHTING,
	weakness = Some(EnergyType.GRASS),
	resistance = Some(EnergyType.FIGHTING),
	retreatCost = 2)