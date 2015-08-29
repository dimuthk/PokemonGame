package src.card.pokemon

import src.move.MoveBuilder._
import play.api.libs.json._
import src.card.Placeholder
import src.card.pokemon.PokemonCard
import src.player.Player


trait Withdrawable extends PokemonCard {

  var withdrawn : Boolean = false

  val testFunc : Int => Int => Int

  override def updateActiveOnTurnSwap(owner : Player, opp : Player) : Unit = {
      if (owner.isTurn) {
          owner.active.get.generalCondition = None
          withdrawn = false
      }
      super.updateActiveOnTurnSwap(owner, opp)
  }

  override def takeDamage(amount : Int) : Unit = withdrawn match {
    case true => ()
    case false => super.takeDamage(amount)
  }

  override def updateBenchOnTurnSwap(owner : Player, opp : Player) : Unit = {
    withdrawn = false
    super.updateBenchOnTurnSwap(owner, opp)
  }

}

object Withdrawable {

  def tryWithdraw(owner : Player, condName : String) : Unit = owner.active.get match {
    case w : Withdrawable => flippedHeads() match {
      case true => {
        owner.notify(w.displayName + " successfully performed " + condName + "!")
        w.withdrawn = true
        w.generalCondition = Some(condName)
      }
      case false => owner.notify(condName + " failed")
    }
    case _ => throw new Exception("Tried to use tryWithdrawable with a non-withdrawable card")
  }

}