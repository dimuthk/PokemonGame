package src.move

import play.api.libs.json._
import src.board.move.MoveCommand
import src.move.MoveBuilder._
import src.card.pokemon.PokemonCard
import src.board.intermediary.IntermediaryRequest
import src.board.intermediary.IntermediaryRequest._
import src.board.intermediary.ClickableCardRequest
import src.board.drag.CustomDragInterpreter
import src.board.state.CustomStateGenerator
import src.card.energy.EnergyCard
import src.card.energy.EnergyType
import src.json.Identifier
import src.json.Jsonable
import src.player.Player


class HyperBeam(
	name : String,
	val dmg : Int,
    totalEnergyReq : Int,
	specialEnergyReq : Map[EnergyType.Value, Int] = Map()) extends Move(
    	name,
    	totalEnergyReq,
    	specialEnergyReq) {

    class DiscardEnergySpecification(
        p : Player, energyCards : Seq[EnergyCard]) extends ClickableCardRequest(
        "Discard Energy",
        "Select the energy card you want to discard.",
        p,
        1,
        energyCards)

    private def hasMultipleEnergyTypes(energyCards : Seq[EnergyCard]) : Boolean = {
        if (energyCards.length > 1) {
            val firstCard = energyCards(0)
            return !energyCards.filter(_.eType != firstCard.eType).isEmpty
        }
        return false;
    }

    def perform = (owner, opp, args) => args.length match {
        case 0 => opp.active.get.energyCards.length match {
            case 0 => standardAttack(owner, opp, dmg)
            case _ => hasMultipleEnergyTypes(opp.active.get.energyCards) match {
                case true => Some(new DiscardEnergySpecification(owner, opp.active.get.energyCards))
                case false => {
                    opp.active.get.energyCards = opp.active.get.energyCards.tail
                    standardAttack(owner, opp, dmg)
                }
            }
        }
        case _ => {
            val energyIndex = args(0).toInt
            opp.active.get.energyCards = opp.active.get.energyCards diff List(opp.active.get.energyCards(energyIndex))
            standardAttack(owner, opp, dmg)
        }
    }

}