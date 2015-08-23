package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.condition.PreventDamageCondition
import src.card.pokemon._
import src.card.Deck

class Squirtle extends PokemonCard(
	"Squirtle",
	"Squirtle-Base-Set-63.jpg",
	Deck.BASE_SET,
	Identifier.SQUIRTLE,
	id = 7,
	maxHp = 40,
	firstMove = Some(new Bubble()),
	secondMove = Some(new Withdraw()),
	energyType = EnergyType.WATER,
	weakness = Some(EnergyType.THUNDER),
	retreatCost = 1)

private class Bubble extends Move(
	"Bubble",
	1,
	Map(EnergyType.WATER -> 1)) {

  override def perform(owner : Player, opp : Player) =
      paralyzeChanceAttack(owner, opp, 10)
}

private class Withdraw extends Move(
	"Withdraw",
	2,
	Map(EnergyType.WATER -> 1)) {

	override def perform(owner : Player, opp : Player) =
	    setGeneralConditionChance(owner, opp, new PreventDamageCondition("Withdraw"))
}