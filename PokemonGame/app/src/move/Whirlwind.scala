package src.move

import play.api.libs.json._
import src.board.move.MoveCommand
import src.move.MoveBuilder._
import src.card.pokemon.PokemonCard
import src.board.intermediary.IntermediaryRequest
import src.board.intermediary.ClickableCardRequest
import src.board.drag.CustomDragInterpreter
import src.board.move.CustomMoveInterpreter
import src.board.state.CustomStateGenerator
import src.card.energy.EnergyCard
import src.card.energy.EnergyType
import src.json.Identifier
import src.json.Jsonable
import src.player.Player


abstract class Whirlwind(
	name : String,
	totalEnergyReq : Int,
	val dmg : Int,
	specialEnergyReq : Map[EnergyType.Value, Int] = Map()) extends Move(
    	name,
    	totalEnergyReq,
    	specialEnergyReq) {

    class NextActiveSpecification(
        p : Player,
        user : PokemonCard,
        benchCards : Seq[PokemonCard]) extends ClickableCardRequest(
        "Choose Active Pokemon",
        user.displayName + " is whisking you away! Select a new active pokemon",
        "FLIP<>MOVE<>ATTACK_FROM_ACTIVE<>1<>",
        p,
        1,
        benchCards)

    override def additionalRequest(owner : Player, opp : Player, args : Seq[String]) : Option[IntermediaryRequest] = {
        val benchCards = opp.bench.toList.flatten
        if (benchCards.length > 1 && args.length == 0) {
            return Some(new NextActiveSpecification(opp, owner.active.get, benchCards))
        }
        return None
    }

    override def performWithAdditional(owner : Player, opp : Player, args : Seq[String]) {
        val benchIndex : Int = args.head.toInt
        var matcher : Int = -1
        standardAttack(owner, opp, dmg)
        for (realIndex : Int <- 0 until 5) {
            if (opp.bench(realIndex).isDefined) {
                matcher = matcher + 1
            }
            if (matcher == benchIndex) {
                opp.swapActiveAndBench(realIndex)
                return
            }
        }
    }

    def perform = (owner, opp) => standardAttack(owner, opp, dmg)

}