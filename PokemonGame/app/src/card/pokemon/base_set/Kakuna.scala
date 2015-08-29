package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Kakuna extends StageOnePokemon(
	"Kakuna",
	"Kakuna-Base-Set-69.jpg",
	Deck.BASE_SET,
	Identifier.KAKUNA,
	id = 14,
	maxHp = 80,
	firstMove = Some(new Stiffen()),
	energyType = EnergyType.GRASS,
	weakness = Some(EnergyType.FIRE),
	retreatCost = 1)

private class PoisonSting extends Move(
	"Poison Sting",
	1,
	Map(EnergyType.GRASS -> 1)) {

  override def perform(owner : Player, opp : Player) = poisonChanceAttack(owner, opp, 10)
}
