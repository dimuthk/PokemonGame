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

class Nidoqueen extends StageTwoPokemon(
    "Nidoqueen",
    "Nidoqueen-Jungle-7.jpg",
    Deck.JUNGLE,
    Identifier.NIDOQUEEN,
    id = 31,
    maxHp = 90,
    firstMove = Some(new Move(
      "Boyfriends",
      2,
      Map(EnergyType.GRASS -> 1)) {
        def perform = (owner, opp, args) =>
          standardAttack(owner, opp, 20 + 20 * owner.existingActiveAndBenchCards.filter(_.id == 34).length)
      }),
    secondMove = Some(new Move(
      "Mega Punch",
      4,
      Map(EnergyType.GRASS -> 2)) {
        def perform = (owner, opp, args) => standardAttack(owner, opp, 50)
      }),
    energyType = EnergyType.GRASS,
    weakness = Some(EnergyType.PSYCHIC),
    retreatCost = 3)
