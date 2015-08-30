package src.move

import play.api.libs.json._
import src.board.move.MoveCommand
import src.move.MoveBuilder._
import src.card.pokemon.PokemonCard
import src.board.intermediary.IntermediaryRequest
import src.board.intermediary.IntermediaryRequest._
import src.board.intermediary.ClickableCardRequest
import src.board.drag.CustomDragInterpreter
import src.board.move.CustomMoveInterpreter
import src.board.state.CustomStateGenerator
import src.card.energy.EnergyCard
import src.card.energy.EnergyType
import src.json.Identifier
import src.json.Jsonable
import src.player.Player


abstract class BenchSelectAttack(
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
        "MOVE<>ATTACK_FROM_ACTIVE<>1<>",
        p,
        numBenchSelects,
        benchCards)

    override def additionalRequest(owner : Player, opp : Player, args : Seq[String]) : Option[IntermediaryRequest] = {
        val benchCards = opp.bench.toList.flatten
        if (benchCards.length > numBenchSelects && args.length == 0) {
            return Some(new NextActiveSpecification(owner, owner.active.get, benchCards))
        }
        return None
    }

    override def performWithAdditional(owner : Player, opp : Player, args : Seq[String]) {
        val benchIndices : Seq[Int] = args.head.split(",").map(_.toInt)
        var matcher : Int = -1
        standardAttack(owner, opp, mainDmg)
        for (i : Int <- benchIndices) {
            val realIndex = getRealIndexFor(i, opp.bench)
            opp.bench(realIndex).get.takeDamage(benchDmg)
        }
    }

    def perform = (owner, opp) => {
        standardAttack(owner, opp, mainDmg)
        for (bc : PokemonCard <- opp.bench.toList.flatten) {
            bc.takeDamage(benchDmg)
        }
    }

}