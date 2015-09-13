package src.card.pokemon.base_set

import src.json.Identifier
import src.board.intermediary.SingleDisplay
import src.board.move.CustomMoveInterpreter
import src.board.state.CustomStateGenerator
import src.move._
import src.card.condition.StatusCondition
import src.move.Status._
import src.card.Card
import src.card.CardUI
import src.card.CardUI._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck
import src.card.Placeholder
import play.api.libs.json._
import play.api.Logger

class Primeape extends StageOnePokemon(
	"Primeape",
	"Primeape-Jungle-43.jpg",
	Deck.JUNGLE,
	Identifier.PRIMEAPE,
	id = 57,
	maxHp = 70,
	firstMove = Some(new Move(
    "Fury Swipes",
    2,
    Map(EnergyType.FIGHTING -> 2)) {
      def perform = (owner, opp, args) => multipleHitAttack(owner, opp, 20, 3)
    }),
	secondMove = Some(new Move(
		"Tantrum",
    3,
    Map(EnergyType.FIGHTING -> 2)) {
			def perform = (owner, opp, args) => {
        standardAttack(owner, opp, 50)
        if (!flippedHeads()) {
          owner.active.get.inflictStatus(StatusCondition.CONFUSED)
        }
      }
    }),
	energyType = EnergyType.FIGHTING,
	weakness = Some(EnergyType.PSYCHIC),
	retreatCost = 1)