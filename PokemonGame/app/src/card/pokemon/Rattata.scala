package src.card.pokemon

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType

class Rattata extends PokemonCard(
	"Rattata",
	"Rattata-Base-Set-61.jpg",
	id = 19,
	maxHp = 30,
	firstMove = Some(new Bite()),
	secondMove = None,
	energyType = EnergyType.COLORLESS,
	weakness = Some(EnergyType.FIGHTING),
	resistance = Some(EnergyType.PSYCHIC),
	retreatCost = 0) {

	override def getIdentifier() = Identifier.RATTATA
}

private class Bite extends Move(
	"Bite",
	1,
	Map(),
	(owner : Player, opp : Player) =>
	  standardAttack(owner, opp, 20))