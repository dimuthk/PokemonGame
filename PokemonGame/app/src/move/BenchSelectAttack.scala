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


class BenchSelectAttack(
	name : String,
	val mainDmg : Int,
    val benchDmg : Int,
    val numBenchSelects : Int,
    totalEnergyReq : Int,
	specialEnergyReq : Map[EnergyType.Value, Int] = Map()) extends Move(
    	name,
    	totalEnergyReq,
    	specialEnergyReq) {

    class NextActiveSpecification(
        p : Player,
        user : PokemonCard,
        benchCards : Seq[PokemonCard]) extends ClickableCardRequest(
        "Select bench cards",
        "Select the bench pokemon you want " + user.displayName + " to attack!",
        p,
        numBenchSelects,
        benchCards)

    def perform = (owner, opp, args) => args.length match {
        case 0 => {
            val benchCards = opp.bench.toList.flatten
            (benchCards.length > numBenchSelects) match {
                case true => Some(new NextActiveSpecification(owner, owner.active.get, benchCards))
                case false => {
                    for (bc : PokemonCard <- opp.bench.toList.flatten) {
                        bc.takeDamage(owner.ownerOfMove(this), benchDmg)
                    }
                    standardAttack(owner, opp, mainDmg)
                }
            }
        }
        case _ => {
            val benchIndices : Seq[Int] = args.head.split(",").map(_.toInt)
            var matcher : Int = -1
            for (i : Int <- benchIndices) {
                val realIndex = getRealIndexFor(i, opp.bench)
                opp.bench(realIndex).get.takeDamage(owner.ownerOfMove(this), benchDmg)
            }
            standardAttack(owner, opp, mainDmg)
        }
    }

}