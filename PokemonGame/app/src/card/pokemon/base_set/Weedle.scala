package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Weedle extends BasicPokemon(
	"Weedle",
	"Weedle-Base-Set-69.jpg",
	Deck.BASE_SET,
	Identifier.WEEDLE,
	id = 13,
	maxHp = 40,
	firstMove = Some(new PoisonSting2()),
	energyType = EnergyType.GRASS,
	weakness = Some(EnergyType.FIRE),
	retreatCost = 1)

private class PoisonSting2 extends Move(
	"Poison Sting",
	1,
	Map(EnergyType.GRASS -> 1)) {

  override def perform(owner : Player, opp : Player) = poisonChanceAttack(owner, opp, 10)
}
