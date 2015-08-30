package src.card.pokemon

import src.card.Card
import src.move.Move
import src.card.energy.EnergyType
import src.move.MoveBuilder._
import play.api.libs.json._
import src.card.Placeholder
import src.card.pokemon.PokemonCard
import src.player.Player


trait Withdrawable extends PokemonCard {

  var withdrawn : Boolean = false

  override def updateCardOnTurnSwap(owner : Player, opp : Player, isActive : Boolean) : Unit = {
    isActive match {
      case true => if (owner.isTurn) {
        owner.active.get.generalCondition = None
        withdrawn = false
      }
      case false => withdrawn = false
    }
    super.updateCardOnTurnSwap(owner, opp, isActive)
  }

  override def calculateDmg(attacker : PokemonCard, baseDmg : Int) : Int = withdrawn match {
    case true => 0
    case false => super.calculateDmg(attacker, baseDmg)
  }

  override def takeDamage(amount : Int) : Unit = withdrawn match {
    case true => ()
    case false => super.takeDamage(amount)
  }

  override def pickUp() : Seq[Card] = {
    withdrawn = false
    return super.pickUp()
  }

}

object Withdrawable {

  class Withdraw(
    condName : String,
    totalEnergyReq : Int,
    specialEnergyReq : Map[EnergyType.Value, Int] = Map()) extends Move(
      condName,
      totalEnergyReq,
      specialEnergyReq) {
        def perform = (owner, opp) => owner.active.get match {
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

}