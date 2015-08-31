package src.card.pokemon.jungle

import src.json.Identifier
import src.move._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.board.intermediary.IntermediaryRequest
import src.board.intermediary.ClickableCardRequest
import src.card.condition.PreventDamageCondition
import src.card.pokemon._
import src.card.Deck

import play.api.Logger

class Pidgeot extends BasicPokemon(
    "Pidgeot",
    "Pidgeot-Jungle-24.jpg",
    Deck.JUNGLE,
    Identifier.PIDGEOT,
    id = 24,
    maxHp = 80,
    firstMove = Some(new Move(
        "Wing Attack",
        2) {
      def perform = (owner, opp, args) => standardAttack(owner, opp, 20)
    }),
    secondMove = Some(new Move(
      "Hurricane",
      3) {
        def perform = (owner, opp, args) => {
          standardAttack(owner, opp, 20)
          if (opp.active.get.currHp > 0) {
            opp.moveActiveToHand()
          }
          None
        }
    }),
    energyType = EnergyType.COLORLESS,
    weakness = Some(EnergyType.THUNDER),
    resistance = Some(EnergyType.FIGHTING),
    retreatCost = 0)
