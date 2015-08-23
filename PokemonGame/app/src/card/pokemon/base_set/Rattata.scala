package src.card.pokemon

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Rattata extends PokemonCard(
	"Rattata",
	"Rattata-Base-Set-61.jpg",
	Deck.BASE_SET,
	Identifier.RATTATA,
	id = 19,
	maxHp = 30,
	firstMove = Some(new Bite()),
	energyType = EnergyType.COLORLESS,
	weakness = Some(EnergyType.FIGHTING),
	resistance = Some(EnergyType.PSYCHIC),
	retreatCost = 0)

private class Bite extends Move(
	"Bite",
	1,
	Map()) {

  override def perform(owner : Player, opp : Player) = standardAttack(owner, opp, 20)

}
