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
	val firstMove: Option[Move],
    val secondMove: Option[Move],
	val energyType : EnergyType.Value,
	val weakness : Option[EnergyType.Value],
    val resistance : Option[EnergyType.Value],
    val retreatCost : Int) extends Card(displayName, imgName) {

  var currHp : Int = maxHp

  var energyCards : Seq[EnergyCard] = List()

	override def toJsonImpl() = Json.obj(
		Identifier.DISPLAY_NAME.toString -> displayName,
		Identifier.MAX_HP.toString -> maxHp,
		Identifier.CURR_HP.toString -> currHp,
		Identifier.IMG_NAME.toString -> imgName,
    	Identifier.ENERGY_CARDS.toString -> cardListToJsArray(energyCards),
    	Identifier.MOVE_ONE_NAME.toString -> (firstMove match {
    		case Some(m : Move) => m.name
    		case None => Identifier.NO_MOVE_NAME.toString
    	}),
    	Identifier.MOVE_ONE_ENABLED.toString -> (firstMove match {
    		case Some(m : Move) => if (!m.disabled && m.hasEnoughEnergy(energyCards)) "" else "disabled"
    		case None => "disabled"
    	}),
    	Identifier.MOVE_TWO_NAME.toString -> (secondMove match {
    		case Some(m : Move) => m.name
    		case None => Identifier.NO_MOVE_NAME.toString
    	}),
    	Identifier.MOVE_TWO_ENABLED.toString -> (secondMove match {
    		case Some(m : Move) => if (!m.disabled && m.hasEnoughEnergy(energyCards)) "" else "disabled"
    		case None => "disabled"
    	}))

	def takeDamage(amount : Int) : Unit = {
    	if (amount <= 0) {
      		return  
    	}
    	currHp = math.max(currHp - amount, 0)
  	}

}
