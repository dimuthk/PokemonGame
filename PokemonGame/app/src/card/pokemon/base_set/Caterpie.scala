package src.card.pokemon

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Caterpie extends PokemonCard(
	"Caterpie",
	"Caterpie-Base-Set-45.jpg",
	Deck.BASE_SET,
	Identifier.CATERPIE,
	id = 10,
	maxHp = 40,
	firstMove = Some(new StringShot()),
	energyType = EnergyType.GRASS,
	weakness = Some(EnergyType.FIRE),
	retreatCost = 1)

private class StringShot extends Move(
	"StringShot",
	1,
	Map(EnergyType.GRASS -> 1)) {

  override def perform(owner : Player, opp : Player) = paralyzeChanceAttack(owner, opp, 10)
}
