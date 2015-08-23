package src.card.pokemon

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Charmander extends PokemonCard(
	"Charmander",
	"Charmander-Base-Set-46.jpg",
	Deck.BASE_SET,
	Identifier.CHARMANDER,
	id = 4,
	maxHp = 50,
	firstMove = Some(new Scratch()),
	secondMove = Some(new Ember()),
	energyType = EnergyType.FIRE,
	weakness = Some(EnergyType.WATER),
	retreatCost = 1)

private class Scratch extends Move(
	"Scratch",
	1,
	Map()) {

  override def perform(owner : Player, opp : Player) =
      standardAttack(owner, opp, 10)
}

private class Ember extends Move(
	"Ember",
	2,
	Map(EnergyType.FIRE -> 1)) {

	override def perform(owner : Player, opp : Player) =
	    energyDiscardAttack(owner, opp, 30, EnergyType.FIRE)
}