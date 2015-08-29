package src.board.drag

import src.board.intermediary.IntermediaryRequest
import src.card.Card
import src.card.energy.EnergyCard
import src.card.pokemon.PokemonCard
import src.board.drag._
import src.player.Player

abstract class DragInterpreter {

    /**
     * Idempotent method returning any additional information needed to process the request.
     */
    def additionalRequest(p : Player, command : DragCommand) : Option[IntermediaryRequest] = None

    def handleDrag(p : Player, move : DragCommand) {
      move match {
        case HandToActive(handIndex : Int) => handToActive(p, handIndex)
        case HandToBench(handIndex : Int, benchIndex : Int) => handToBench(p, handIndex, benchIndex)
        case ActiveToBench(benchIndex : Int) => activeToBench(p, benchIndex : Int)
        case BenchToActive(benchIndex : Int) => benchToActive(p, benchIndex : Int)
        case BenchToBench(benchIndex1 : Int, benchIndex2 : Int) => benchToBench(p, benchIndex1, benchIndex2)
        case Intermediary(cmd : Seq[String]) => handleIntermediary(p, cmd)
      }
    }

    /**
     * Notifies that bench card positioned at index1 is attempting to be moved to index2. Assumes
     * that benchIndex1 points to a non-null bench card.
     */
    def benchToBench(p : Player, benchIndex1 : Int, benchIndex2 : Int) : Unit = ()

    /**
     * Notifies that bench card positioned at index is attempting to be moved to active slot. Assumes
     * that benchIndex points to a non-null bench card.
     */
    def benchToActive(p : Player, benchIndex : Int) : Unit = ()

    /**
     * Notifies that active card is attempting to be moved to bench card position at index. Assumes
     * active card is non-null.
     */
    def activeToBench(p : Player, benchIndex : Int) : Unit = ()

    /**
     * Notifies that a card from the hand is attempting to be moved to the active slot. Assumes the hand
     * card exists.
     */
    def handToActive(p : Player, handIndex : Int) : Unit = ()

    /**
     * Notiies that a card from the hand is attempting to be moved to bench card positioned at index.
     * Assumes the hand card exists.
     */
    def handToBench(p : Player, handIndex : Int, benchIndex : Int) : Unit = ()

    def handleIntermediary(p : Player, cmd : Seq[String]) : Unit = ()

}
