package src.move

import src.card.pokemon.PokemonCard
import play.api.libs.json._
import src.board.move.MoveCommand
import src.board.intermediary.IntermediaryRequest
import src.board.drag.CustomDragInterpreter
import src.board.state.CustomStateGenerator
import src.card.energy.EnergyCard
import src.card.energy.EnergyType
import src.json.Identifier
import src.json.Jsonable
import src.player.Player

import play.api.Logger

/**
 * Defines a move that can be performed by a Pokemon. Moves can be both active
 * and reactive. In the active case, they execute some function when explicitly
 * instructed by the player. In the reactive case, they intercept an incoming
 * command and handle it in some way.
 */
abstract class Move(
	val name : String,
	val totalEnergyReq : Int,
	val specialEnergyReq : Map[EnergyType.Value, Int] = Map(),
  val dragInterpreter : Option[CustomDragInterpreter] = None,
  val stateGenerator : Option[CustomStateGenerator] = None) extends Jsonable {

  var status : Status.Value = Status.DISABLED

  //def additionalRequest(owner : Player, opp : Player, args : Seq[String]) : Option[IntermediaryRequest] = None

  def hasEnoughEnergy(p : Player, energyCards : Seq[EnergyCard]) : Boolean = {
		val total = energyCards.map { card => card.energyCount }.sum
		if (total < totalEnergyReq) {
      Logger.debug(total + "<" + totalEnergyReq)
			return false
		}

		for ((energyType, reqCnt) <- specialEnergyReq) {
			val specialCntTotal = energyCards
				.filter(_.eType == energyType)
				.map { card => card.energyCount }.sum
			if (specialCntTotal < reqCnt) {
        Logger.debug("special " + specialCntTotal + "<" + reqCnt)
				return false
			}
    	}
      Logger.debug(name + " is okay")
    	return true
  }

  def update : (Player, Player, PokemonCard, Boolean, Boolean)  => Unit
    = (owner, opp, pc, turnSwapped, isActive) => status = (
      // Status is enabled iff it's your turn, this card is in the active slot,
      // the card has enough energy to perform the move, and you don't currently
      // have an activated power on the board.
      owner.isTurn,
      isActive,
      hasEnoughEnergy(owner, pc.energyCards),
      owner.cardWithActivatedPower == None) match {
        case (true, true, true, true) =>  Status.ENABLED
        case _ => Status.DISABLED
      }
  
  /*def update(owner : Player, opp : Player, pc : PokemonCard, turnSwapped : Boolean, isActive : Boolean) : Unit =
      // Status is enabled iff it's your turn, this card is in the active slot,
      // the card has enough energy to perform the move, and you don't currently
      // have an activated power on the board.
      status = (
        owner.isTurn,
        isActive,
        hasEnoughEnergy(owner, pc.energyCards),
        owner.cardWithActivatedPower == None) match {
        case (true, true, true, true) =>  Status.ENABLED
        case _ => Status.DISABLED
      }*/

  def perform : (Player, Player, Seq[String]) => Option[IntermediaryRequest]

  def performWithAdditional(owner : Player, opp : Player, cmds : Seq[String]) {
    throw new Exception("performWithAdditional() called with from base move client")
  }

  override def toJsonImpl() = Json.obj(
    Identifier.MOVE_NAME.toString -> name,
    Identifier.MOVE_STATUS.toString -> status)

  override def getIdentifier() = Identifier.MOVE

}

/**
 * Enabled: corresponding to a general clickable action which does something and
 *   then terminates.
 * Disabled: corresponding to a state where the action is clickable, but currently
 *   disabled for whatever reason.
 * Activatable: corresponding to a clickable action which will activate something
 *   on the board which persists.
 * Activated: corresponding to a state where some persistent background state has
 *   been activated. You should be able to click this and revert the state to
 *   activatable.
 * Passive: corresponding to a persistent background effect which by default is
 *   activated. It may still be disabled, however.
 */
object Status extends Enumeration {

  type Status = Value

  val ENABLED, DISABLED, ACTIVATABLE, ACTIVATED, PASSIVE = Value
}
