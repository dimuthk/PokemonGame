package src.card.pokemon

import play.api.libs.json._
import src.card.Card
import src.card.Deck
import src.card.condition.GeneralCondition
import src.card.condition.PoisonStatus
import src.card.condition.StatusCondition
import src.card.energy.EnergyCard
import src.card.energy.EnergyType
import src.json.Identifier
import src.move.Move

abstract class PokemonCard(
	displayName : String,
	imgName : String,
  deck : Deck.Value,
  val identifier : Identifier.Value,
	val id : Int,
	val maxHp : Int,
	val firstMove: Option[Move] = None,
  val secondMove: Option[Move] = None,
	val energyType : EnergyType.Value,
	val weakness : Option[EnergyType.Value] = None,
  val resistance : Option[EnergyType.Value] = None,
  val retreatCost : Int,
  val evolutionStage : EvolutionStage.Value = EvolutionStage.BASIC) extends Card(displayName, imgName, deck) {

  var currHp : Int = maxHp

  var energyCards : Seq[EnergyCard] = List()

  var preEvolution : Option[PokemonCard] = None

  var poisonStatus : Option[PoisonStatus.Value] = None

  var statusCondition : Option[StatusCondition.Value] = None

  var generalCondition : Option[GeneralCondition] = None

  override def getIdentifier() = identifier

	override def toJsonImpl() = super.toJsonImpl() ++ Json.obj(
		Identifier.DISPLAY_NAME.toString -> displayName,
		Identifier.MAX_HP.toString -> maxHp,
		Identifier.CURR_HP.toString -> currHp,
		Identifier.IMG_NAME.toString -> imgName,
    Identifier.ENERGY_CARDS.toString -> cardListToJsArray(energyCards),
    Identifier.ENERGY_TYPE.toString -> energyType,
    Identifier.MOVES.toString -> optionMoveListToJsArray(List(firstMove, secondMove)),
    Identifier.POISON_STATUS.toString -> (poisonStatus match {
      case Some(p : PoisonStatus.Value) => p.toString
      case None => "None"
    }),
    Identifier.STATUS_CONDITION.toString -> (statusCondition match {
      case Some(s : StatusCondition.Value) => s.toString
      case None => "None"
    }),
    Identifier.GENERAL_CONDITION.toString -> (generalCondition match {
      case Some(g : GeneralCondition) => g.name
      case None => "None"
    }))

  def getExistingMoves() : Seq[Move] = {
    return List(firstMove, secondMove).flatten
  }

  def getTotalEnergy(oEType : Option[EnergyType.Value] = None) : Int = {
    oEType match {
      case Some(eType) => return energyCards.filter(_.eType == eType).length
      case None => return energyCards.length
    }
  }

  def evolveOver(pc : PokemonCard) {
    energyCards = pc.energyCards
    pc.energyCards = Nil
    preEvolution = Some(pc)
  }

  def discardEnergy(eType : EnergyType.Value, cnt : Int = 1) : Seq[EnergyCard] = {
    var discardedCards : Seq[EnergyCard] = List()
    var currentCnt = cnt
    for (i <- 0 until energyCards.length) {
      if (eType == EnergyType.COLORLESS || energyCards(i).eType == eType) {
        discardedCards = discardedCards ++ List(energyCards(i))
        currentCnt = currentCnt - 1
      }
      if (currentCnt == 0) {
        energyCards = energyCards diff discardedCards
        return discardedCards
      }
    }
    throw new Exception("Not enough energy for discarding")
  }

	def takeDamage(amount : Int) : Unit = {
    	if (amount <= 0) {
      		return  
    	}
    	currHp = math.max(currHp - amount, 0)
  }

  def heal(amount : Int) : Unit = {
    if (amount <= 0) {
      return
    }
    currHp = math.min(currHp + amount, maxHp)
  }

  def isEvolutionOf(pc : PokemonCard) : Boolean = {
    return if(evolutionStage == EvolutionStage.BASIC) false else pc.id == (id - 1)
  }

}

object EvolutionStage extends Enumeration {

  type EvolutionStage = Value

  val BASIC, STAGE_ONE, STAGE_TWO = Value
}
