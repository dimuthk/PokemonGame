package src.card.pokemon

import play.api.libs.json._
import src.card.Card
import src.card.energy.EnergyCard
import src.card.energy.EnergyType
import src.json.Identifier
import src.move.Move

abstract class PokemonCard(
	displayName : String,
	imgName : String,
	val id : Int,
	val maxHp : Int,
	val firstMove: Option[Move] = None,
  val secondMove: Option[Move] = None,
	val energyType : EnergyType.Value,
	val weakness : Option[EnergyType.Value] = None,
  val resistance : Option[EnergyType.Value] = None,
  val retreatCost : Int,
  val evolutionStage : EvolutionStage.Value = EvolutionStage.BASIC) extends Card(displayName, imgName) {

  var currHp : Int = maxHp

  var energyCards : Seq[EnergyCard] = List()

  var preEvolution : Option[PokemonCard] = None

  var poisonStatus : Option[PoisonStatus.Value] = None

  var statusCondition : Option[StatusCondition.Value] = None

	override def toJsonImpl() = Json.obj(
		Identifier.DISPLAY_NAME.toString -> displayName,
		Identifier.MAX_HP.toString -> maxHp,
		Identifier.CURR_HP.toString -> currHp,
		Identifier.IMG_NAME.toString -> imgName,
    Identifier.ENERGY_CARDS.toString -> cardListToJsArray(energyCards),
    Identifier.MOVES.toString -> optionMoveListToJsArray(List(firstMove, secondMove)),
    Identifier.POISON_STATUS.toString -> (poisonStatus match {
      case Some(p : PoisonStatus.Value) => p.toString
      case None => "None"
    }))

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

  def isEvolutionOf(pokemon : PokemonCard) : Boolean = return false

}

object EvolutionStage extends Enumeration {

  type EvolutionStage = Value

  val BASIC, STAGE_ONE, STAGE_TWO = Value
}

object StatusCondition extends Enumeration {

  type StatusCondition = Value

  val PARALYZED, CONFUSED, ASLEEP = Value
}

object PoisonStatus extends Enumeration {

  type PoisonStatus = Value

  val POISONED= Value("Poisoned")
  val TOXIC = Value("Toxic")
}
