package src.card.pokemon

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType

class Machop extends PokemonCard(
	"Machop",
	"Machop-Base-Set-52.jpg",
	id = 66,
	maxHp = 50,
	firstMove = Some(new LowKick()),
	secondMove = None,
	energyType = EnergyType.FIGHTING,
	weakness = Some(EnergyType.PSYCHIC),
	resistance = None,
	retreatCost = 1) {

	override def getIdentifier() = Identifier.MACHOP
}

private class LowKick extends Move(
    "Low Kick",
    1,
    Map(EnergyType.FIGHTING -> 1),
    (owner : Player, opp : Player) =>
      standardAttack(owner, opp, 20))