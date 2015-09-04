package src.card.pokemon.base_set

import src.card.condition.StatusCondition
import src.json.Identifier
import src.board.drag.CustomDragInterpreter
import src.board.state.CustomStateGenerator
import src.move._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.CardUI
import src.card.CardUI._
import src.card.pokemon._
import src.card.Deck
import play.api.Logger

class MagmarFossil extends BasicPokemon(
	"Magmar",
	"Magmar-Fossil-39.jpg",
	Deck.FOSSIL,
	Identifier.MAGMAR_FOSSIL,
	id = 126,
	maxHp = 70,
	firstMove = Some(new Smokescreen(
      "Smokescreen",
      dmg = 10,
      1,
      Map(EnergyType.FIRE -> 1))),
	secondMove = Some(new Move(
    "Smog",
    2,
    Map(EnergyType.FIRE -> 2)) {
      def perform = (owner, opp, args) => poisonAttackChance(owner, opp, 20)
    }),
	energyType = EnergyType.FIRE,
	resistance = Some(EnergyType.WATER),
	retreatCost = 1)
