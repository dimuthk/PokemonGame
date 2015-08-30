package src.move

import src.card.Card
import play.api.libs.json._
import src.board.move.MoveCommand
import src.move.MoveBuilder._
import src.card.pokemon.PokemonCard
import src.board.intermediary.IntermediaryRequest
import src.board.intermediary.SpecificClickableCardRequest
import src.board.drag.CustomDragInterpreter
import src.board.move.CustomMoveInterpreter
import src.board.state.CustomStateGenerator
import src.card.energy.EnergyCard
import src.card.energy.EnergyType
import src.json.Identifier
import src.json.Jsonable
import src.player.Player


abstract class CallForFamily(
	name : String,
    moveNum : Int,
    selector : Card => Boolean,
	totalEnergyReq : Int,
	specialEnergyReq : Map[EnergyType.Value, Int] = Map()) extends Move(
    	name,
    	totalEnergyReq,
    	specialEnergyReq) {

    class NextActiveSpecification(
        p : Player,
        user : PokemonCard,
        deck : Seq[Card]) extends SpecificClickableCardRequest(
        "Call For Family",
        user.displayName + " is whisking you away! Select a new active pokemon",
        "MOVE<>ATTACK_FROM_ACTIVE<>" + moveNum + "<>",
        p,
        1,
        deck) {
            def isSelectable = selector
        }

    override def update(owner : Player, opp : Player, pc : PokemonCard, turnSwapped : Boolean, isActive : Boolean) : Unit = {
        super.update(owner, opp, pc, turnSwapped, isActive)
        if (owner.benchIsFull) {
            status = Status.DISABLED
        }
    }

    override def additionalRequest(owner : Player, opp : Player, args : Seq[String]) : Option[IntermediaryRequest] = {
        if (args.length > 0) {
            return None
        }
        return Some(new NextActiveSpecification(owner, owner.active.get, owner.deck))
    }

    def perform = (owner, opp) => ()

    override def performWithAdditional(owner : Player, opp : Player, args : Seq[String]) {
        val deckIndex = args(0).toInt
        for (i : Int <- 0 until 5) {
            if (owner.bench(i).isEmpty) {
                owner.moveDeckToBench(deckIndex, i)
                return
            }
        }
        throw new Exception("Called for family with a full bench")
    }

}