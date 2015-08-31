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

class Nidorina extends StageOnePokemon(
    "Nidorina",
    "Nidorina-Jungle-40.jpg",
    Deck.JUNGLE,
    Identifier.NIDORINA,
    id = 30,
    maxHp = 70,
    firstMove = Some(new Move(
      "Supersonic",
      1,
      Map(EnergyType.GRASS -> 1)) {
        def perform = (owner, opp, args) => confuseAttackChance(owner, opp)
      }),
    secondMove = Some(new Move(
      "Double Kick",
      3,
      Map(EnergyType.GRASS -> 1)) {
        def perform = (owner, opp, args) => multipleHitAttack(owner, opp, 30, 2)
      }),
    energyType = EnergyType.GRASS,
    weakness = Some(EnergyType.PSYCHIC),
    retreatCost = 1)
