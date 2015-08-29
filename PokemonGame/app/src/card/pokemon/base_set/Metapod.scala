package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.card.pokemon.Withdrawable._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.board.move.PreventDamageInterpreter
import src.card.condition.PreventDamageCondition
import src.card.pokemon._
import src.card.Deck

class Metapod extends StageOnePokemon(
    "Metapod",
    "Metapod-Base-Set-54.jpg",
    Deck.BASE_SET,
    Identifier.METAPOD,
    id = 11,
    maxHp = 70,
    firstMove = Some(new Withdraw(
    "Stiffen",
    2) {}),
    secondMove = Some(new Move(
      "Stun Spore",
      2,
      Map(EnergyType.GRASS -> 2)) {
        def perform = (owner, opp) => paralyzeChanceAttack(owner, opp, 20)
      }),
    energyType = EnergyType.GRASS,
    weakness = Some(EnergyType.FIRE),
    retreatCost = 2) with Withdrawable