package src.card.pokemon.base_set

import src.card.Card
import src.card.condition._
import src.board.intermediary._
import src.json.Identifier
import src.card.energy._
import src.move._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Venomoth extends StageOnePokemon(
	"Venomoth",
	"Venomoth-Jungle-13.jpg",
	Deck.JUNGLE,
	Identifier.VENOMOTH,
	id = 49,
	maxHp = 70,
	firstMove = Some(new Shift()),
	secondMove = Some(new Move(
		"Venom Powder",
        2,
        Map(EnergyType.GRASS -> 2)) {
			def perform = (owner, opp, args) => {
				if (flippedHeads()) {
					opp.active.get.statusCondition = Some(StatusCondition.CONFUSED)
					opp.active.get.poisonStatus = Some(PoisonStatus.POISONED)
				}
				None
			}
        }),
	energyType = EnergyType.GRASS,
	weakness = Some(EnergyType.FIRE),
	retreatCost = 1) {

	var currEnergyType : EnergyType.Value = EnergyType.GRASS

	override def energyType = currEnergyType

	override def updateCardOnTurnSwap(owner : Player, opp : Player, isActive : Boolean) {
		super.updateCardOnTurnSwap(owner, opp, isActive)
		if (currEnergyType != EnergyType.GRASS) {
			generalCondition = Some(generalCondition.getOrElse("") + "Shifted energy type: " + energyType)
		}
	}

	override def pickUp() : Seq[Card] = {
		currEnergyType = EnergyType.GRASS
		return super.pickUp()
	}

}

private class Shift extends ActivePokemonPower("Shift") {

	class TypeChooseSpecification(
        p : Player) extends ClickableCardRequest(
        "Select Energy",
        "Choose an energy type to switch to.",
        p,
        1,
        energyList)

    val energyList = List(
    	new FireEnergy(),
    	new WaterEnergy(),
    	new ThunderEnergy(),
    	new FightingEnergy(),
    	new PsychicEnergy(),
    	new GrassEnergy(),
    	new DoubleColorlessEnergy())

	var usedShift : Boolean = false
	var requestedShift : Boolean = false

	private def findUser(owner : Player) : Option[Venomoth] = {
		owner.existingActiveAndBenchCards.foreach {
			case v : Venomoth => if (v.firstMove.get == this) {
				return Some(v)
			}
			case _ => ()
		}
		return None
	}

	override def update = (owner, opp, pc, turnSwapped, isActive) => {
    	super.update(owner, opp, pc, turnSwapped, isActive)
    	if (turnSwapped && owner.isTurn) {
      		usedShift = false
    	}
    	if (usedShift) {
      		status = Status.DISABLED
    	}
  	}

	override def perform = (owner, opp, args) => args.length match {
		case 0 => {
			requestedShift = true
			Some(new TypeChooseSpecification(owner))
		}
		case _ => {
			usedShift match {
				case true => throw new Exception("Tried to use shift a second time on same turn")
				case false => findUser(owner) match {
					case Some(v) => {
						v.currEnergyType = energyList(args(0).toInt).eType
						v.generalCondition = Some(v.generalCondition.getOrElse("") + "Shifted energy type: " + v.energyType)
					}
					case None => throw new Exception("Could not find venomoth which used shift")
				}
			}
			usedShift = true
			requestedShift = false
			None
		}
	}
}