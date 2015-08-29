package src.card.pokemon

import play.api.libs.json._
import src.card.Placeholder
import src.card.Card
import src.card.pokemon.StageOnePokemon
import src.card.Deck
import src.card.condition.GeneralCondition
import src.card.condition.PoisonStatus
import src.card.condition.StatusCondition
import src.card.energy.EnergyCard
import src.card.energy.EnergyType
import src.json.Identifier
import src.move.Move

abstract class StageTwoPokemon(
	displayName : String,
	imgName : String,
  deck : Deck.Value,
  identifier : Identifier.Value,
	id : Int,
	maxHp : Int,
	firstMove: Option[Move] = None,
  secondMove: Option[Move] = None,
	energyType : EnergyType.Value,
	weakness : Option[EnergyType.Value] = None,
  resistance : Option[EnergyType.Value] = None,
  retreatCost : Int) extends EvolvedPokemon(
    displayName,
    imgName,
    deck,
    identifier,
    id,
    maxHp,
    firstMove,
    secondMove,
    energyType,
    weakness,
    resistance,
    retreatCost) {

    def isEvolutionOf(pc : PokemonCard) : Boolean = pc match {
      case sc : StageOnePokemon => id - 1 == pc.id
      case _ => false
    }

}