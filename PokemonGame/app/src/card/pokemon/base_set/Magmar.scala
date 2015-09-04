package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Magmar extends BasicPokemon(
	"Magmar",
	"Magmar-Base-Set-36.jpg",
	Deck.BASE_SET,
	Identifier.MAGMAR,
	id = 126,
	maxHp = 50,
	firstMove = Some(new Move(
		"Fire Punch",
		2,
		Map(EnergyType.FIRE -> 2)) {
			def perform = (owner, opp, args) => standardAttack(owner, opp, 30)
		}),
	secondMove = Some(new Move(
      "Flamethrower",
      3,
      Map(EnergyType.FIRE -> 2)) {
        def perform = (owner, opp, args) => energyDiscardAttack(owner, opp, 50, EnergyType.FIRE)
      }),
	energyType = EnergyType.FIRE,
	weakness = Some(EnergyType.WATER),
	retreatCost = 2)