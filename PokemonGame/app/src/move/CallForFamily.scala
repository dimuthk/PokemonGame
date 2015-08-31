package src.move

import src.card.Card
import play.api.libs.json._
import src.board.move.MoveCommand
import src.move.MoveBuilder._
import src.card.pokemon.PokemonCard
import src.board.intermediary.IntermediaryRequest
import src.board.intermediary.SpecificClickableCardRequest
import src.board.drag.CustomDragInterpreter
import src.board.state.CustomStateGenerator
import src.card.energy.EnergyCard
import src.card.energy.EnergyType
import src.json.Identifier
import src.json.Jsonable
import src.player.Player


abstract class CallForFamily(
	name : String,
    selector : Card => Boolean,
	totalEnergyReq : Int,
	specialEnergyReq : Map[EnergyType.Value, Int] = Map()) extends Move(
    	name,
    	totalEnergyReq,
    	specialEnergyReq) {

    class CallForFamilySpecification(
        p : Player,
        deck : Seq[Card]) extends SpecificClickableCardRequest(
        "Call For Family",
        "Select a pokemon to add to your bench.",
        p,
        1,
        deck) {
            def isSelectable = selector
        }

    override def update = (owner, opp, pc, turnSwapped, isActive) => {
        super.update(owner, opp, pc, turnSwapped, isActive)
        if (owner.benchIsFull) {
            status = Status.DISABLED
        }
    }

    def perform = (owner, opp, args) => args.length match {
        case 0 => Some(new CallForFamilySpecification(owner, owner.deck))
        case _ => {
            val deckIndex = args(0).toInt
            def moveDeck : Int => Unit = (i) => owner.bench(i).isEmpty match {
                case true => owner.moveDeckToBench(deckIndex, i)
                case false => moveDeck(i + 1)
            }
            moveDeck(0)
            None
        }
    }

}