package src.move

import play.api.libs.json._
import src.board.move.MoveCommand
import src.move.MoveBuilder._
import src.card.pokemon.PokemonCard
import src.board.intermediary.IntermediaryRequest._
import src.board.intermediary.IntermediaryRequest
import src.board.intermediary.ClickableCardRequest
import src.board.drag.CustomDragInterpreter
import src.board.state.CustomStateGenerator
import src.card.energy.EnergyCard
import src.card.energy.EnergyType
import src.json.Identifier
import src.json.Jsonable
import src.player.Player


class Whirlwind(
	name : String,
    val ownerChooses : Boolean,
	val dmg : Int,
    totalEnergyReq : Int,
	specialEnergyReq : Map[EnergyType.Value, Int] = Map(),
    val onFlip : Boolean = false,
    val switchOwner : Boolean = false,
    val selfDmg : Int = 0) extends Move(
    	name,
    	totalEnergyReq,
    	specialEnergyReq) {

    class NextActiveSpecification(
        p : Player,
        userName : String,
        benchCards : Seq[PokemonCard]) extends ClickableCardRequest(
        "Choose Active Pokemon",
        userName + " is whisking you away! Select a new active pokemon",
        p,
        1,
        benchCards)

    def perform = (owner, opp, args) => args.length match {
        case 0 => (onFlip && !flippedHeads()) match {
            // Failed the bench swap attempt. Do the damage and return
            case true => attackAndHurtSelf(owner, opp, dmg, selfDmg)
            case false => {
                val benchCards = if (switchOwner) owner.bench.toList.flatten else opp.bench.toList.flatten
                benchCards.length match {
                    // No cards to swap. Just do the damage
                    case 0 => attackAndHurtSelf(owner, opp, dmg, selfDmg)
                    // Only one bench card. Do the damage and swap
                    case 1 => {
                        attackAndHurtSelf(owner, opp, dmg, selfDmg)
                        val p = if (switchOwner) owner else opp
                        p.swapActiveAndBench(p.bench.indexOf(Some(benchCards(0))))
                        None
                    }
                    // Ambiguous. Have the player choose and come back
                    case _ => (ownerChooses, switchOwner) match {
                        case (true, _) => Some(new NextActiveSpecification(owner, owner.active.get.displayName, benchCards))
                        case (false, false) => Some(new NextActiveSpecification(opp, owner.active.get.displayName, benchCards))
                        case _ => throw new Exception("unsupported")
                    }
                }
            }
        }
        case _ => {
            val rawIndex : Int = args.head.toInt
            var matcher : Int = -1
            val realIndex = getRealIndexFor(rawIndex, opp.bench)
            attackAndHurtSelf(owner, opp, dmg, selfDmg)
            opp.swapActiveAndBench(realIndex)
            None
        }
    }

}