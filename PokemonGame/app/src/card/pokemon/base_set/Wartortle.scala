package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.board.move.PreventDamageInterpreter
import src.card.condition.PreventDamageCondition
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Wartortle extends StageOnePokemon(
    "Wartortle",
    "Wartortle-Base-Set-42.jpg",
    Deck.BASE_SET,
    Identifier.IVYSAUR,
    id = 8,
    maxHp = 70,
    firstMove = Some(new WithdrawWortortle()),
    secondMove = Some(new WartortleBite()),
    energyType = EnergyType.WATER,
    weakness = Some(EnergyType.THUNDER),
    retreatCost = 1)

private class WithdrawWortortle extends Move(
  "Withdraw",
  2,
  Map(EnergyType.WATER -> 1),
  moveInterpreter = Some(new PreventDamageInterpreter())) {

  override def perform(owner : Player, opp : Player) =
      activateMoveInterpreterChance(owner, this, "Withdraw")
}

private class WartortleBite extends Move(
  "Bite",
  3,
  Map(EnergyType.WATER -> 1)) {

  override def perform(owner : Player, opp : Player) = standardAttack(owner, opp, 40)

}

