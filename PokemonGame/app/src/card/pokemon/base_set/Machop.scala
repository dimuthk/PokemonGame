package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Machop extends PokemonCard(
	"Machop",
	"Machop-Base-Set-52.jpg",
	Deck.BASE_SET,
	Identifier.MACHOP,
	id = 66,
	maxHp = 50,
	firstMove = Some(new LowKick()),
	energyType = EnergyType.FIGHTING,
	weakness = Some(EnergyType.PSYCHIC),
	retreatCost = 1)

private class LowKick extends Move(
    "Low Kick",
    1,
    Map(EnergyType.FIGHTING -> 1)) {

  override def perform(owner : Player, opp : Player) = standardAttack(owner, opp, 20)

}
