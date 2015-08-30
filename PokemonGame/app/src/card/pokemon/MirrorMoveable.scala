package src.card.pokemon

import src.card.Card
import src.card.energy.EnergyType
import src.move.Move
import src.move.Status
import src.move.MoveBuilder._
import play.api.libs.json._
import src.card.Placeholder
import src.card.pokemon.PokemonCard
import src.player.Player


trait MirrorMoveable extends PokemonCard {

  var lastAttack : Int = -1

  override def updateCardOnTurnSwap(owner : Player, opp : Player, isActive : Boolean) : Unit = {
    isActive match {
      case true => if (opp.isTurn) {
        lastAttack = -1
        generalCondition = None
      }
      case false => {
        lastAttack = -1
        generalCondition = None
      }
    }
    super.updateCardOnTurnSwap(owner, opp, isActive)
  }

  override def takeDamage(amount : Int) {
    lastAttack = amount
    generalCondition = Some("Mirror Move: " + lastAttack + "damage")
    super.takeDamage(amount)
  }

  override def pickUp() : Seq[Card] = {
    lastAttack = -1
    return super.pickUp()
  }

}

object MirrorMoveable {

  class MirrorMove(
  totalEnergyReq : Int,
  specialEnergyReq : Map[EnergyType.Value, Int] = Map()) extends Move(
  "Mirror Move",
    totalEnergyReq,
    specialEnergyReq) {

    def perform = (owner, opp) => owner.active.get match {
      case m : MirrorMoveable => m.lastAttack match {
        case -1 => throw new Exception("Tried to use mirror move when no previous attack")
        case _ => standardAttack(owner, opp, m.lastAttack)
      }
      case _ => throw new Exception("Tried to use mirror move with a non-mirror move card")
    }

    override def update(owner : Player, opp : Player, pc : PokemonCard, turnSwapped : Boolean, isActive : Boolean) : Unit = {
      super.update(owner, opp, pc, turnSwapped, isActive)
      // The move is disabled if no attack was made previously.
      pc match {
        case m : MirrorMoveable => m.lastAttack match {
          case -1 => status = Status.DISABLED
          case _ => ()
        }
        case _ => throw new Exception("Tried to update mirror move with a non-mirror move card")
      }
    }

  }
}

