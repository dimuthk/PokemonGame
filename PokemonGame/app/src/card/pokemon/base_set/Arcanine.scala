package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Arcanine extends StageOnePokemon(
	"Arcanine",
	"Arcanine-Base-Set-23.jpg",
	Deck.BASE_SET,
	Identifier.ARCANINE,
	id = 59,
	maxHp = 100,
	firstMove = Some(new Move(
		"Flamethrower",
		3,
		Map(EnergyType.FIRE -> 2)) {
			def perform = (owner, opp, args) => energyDiscardAttack(owner, opp, 50, EnergyType.FIRE)
		}),
	secondMove = Some(new Move(
		"Take Down",
		4,
		Map(EnergyType.FIRE -> 2)) {
			def perform = (owner, opp, args) => attackAndHurtSelf(owner, opp, 80, 30)
		}),
	energyType = EnergyType.FIRE,
	weakness = Some(EnergyType.WATER),
	retreatCost = 3)