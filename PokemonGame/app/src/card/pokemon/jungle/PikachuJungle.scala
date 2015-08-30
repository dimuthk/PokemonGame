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

class PikachuJungle extends BasicPokemon(
    "Pikachu",
    "Pikachu-Jungle-60.jpg",
    Deck.JUNGLE,
    Identifier.PIKACHU_JUNGLE,
    id = 25,
    maxHp = 50,
    firstMove = Some(new BenchSelectAttack(
      "Spark",
      mainDmg = 20,
      benchDmg = 10,
      numBenchSelects = 1,
      2,
      Map(EnergyType.THUNDER -> 2)) {}),
    energyType = EnergyType.THUNDER,
    weakness = Some(EnergyType.FIGHTING),
    retreatCost = 1)
