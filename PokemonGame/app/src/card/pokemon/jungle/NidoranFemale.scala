package src.card.pokemon.jungle

import src.json.Identifier
import src.move._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.board.move.DefaultMoveInterpreter
import src.board.move.CustomMoveInterpreter
import src.board.intermediary.IntermediaryRequest
import src.board.intermediary.ClickableCardRequest
import src.card.condition.PreventDamageCondition
import src.card.pokemon._
import src.card.Deck

import play.api.Logger

class NidoranFemale extends BasicPokemon(
    "Nidoran",
    "Nidoran-F-Jungle-57.jpg",
    Deck.JUNGLE,
    Identifier.NIDORAN_FEMALE,
    id = 29,
    maxHp = 60,
    firstMove = Some(new Move(
      "Fury Swipes",
      1,
      Map(EnergyType.GRASS -> 1)) {
        def perform = (owner, opp) => multipleHitAttack(owner, opp, 10, 3)
      }),
    secondMove = Some(new CallForFamily(
      "Call For Family",
      moveNum = 2,
      selector = (card) => card match {
        case p : PokemonCard => p.id == 29 || p.id == 32
        case _ => false
      },
      totalEnergyReq = 2,
      Map(EnergyType.GRASS -> 2)) {}),
    energyType = EnergyType.GRASS,
    weakness = Some(EnergyType.PSYCHIC),
    retreatCost = 1)
