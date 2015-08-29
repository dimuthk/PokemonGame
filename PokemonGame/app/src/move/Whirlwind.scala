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
    	specialEnergyReq,
    	moveInterpreter = Some(new WhirlwindInterpreter(dmg))) {

    class NextActiveSpecification(
        p : Player,
        user : PokemonCard,
        benchCards : Seq[PokemonCard]) extends ClickableCardRequest(
        "Choose Active Pokemon",
        user.displayName + " is whisking you away! Select a new active pokemon",
        "FLIP<>MOVE<>INTERMEDIARY<>",
        p,
        1,
        benchCards)

    override def additionalRequest(owner : Player, opp : Player) : Option[IntermediaryRequest] = {
        val benchCards = opp.bench.toList.flatten
        if (benchCards.length > 1) {
            moveInterpreter.get.isActive = true
            return Some(new NextActiveSpecification(opp, owner.active.get, benchCards))
        }
        return None
    }

    def perform = (owner, opp) => standardAttack(owner, opp, dmg)

}

class WhirlwindInterpreter(val dmg : Int) extends CustomMoveInterpreter {

    override def handleIntermediary(owner : Player, opp : Player, cmds : Seq[String]) : Unit = {
        // the response comes from a flattened bench, so you need to convert back
        val benchIndex : Int = cmds(0).toInt
        var matcher : Int = -1
        standardAttack(owner, opp, dmg)
        for (realIndex : Int <- 0 until 5) {
            if (opp.bench(realIndex).isDefined) {
                matcher = matcher + 1
            }
            if (matcher == benchIndex) {
                var temp : Option[PokemonCard] = opp.active
                opp.active = opp.bench(realIndex)
                opp.bench(realIndex) = temp
                flipTurn(opp, owner)
                return
            }
        }
        isActive = false
    }

    def attack(owner : Player, opp : Player, move : Move) : Unit = ()
}
