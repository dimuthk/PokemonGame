package src.board.drag

import src.board.intermediary.IntermediaryRequest
import src.card.Card
import src.card.energy.EnergyCard
import src.card.pokemon.PokemonCard
import src.board.drag._
import src.player.Player

abstract class DragInterpreter {

    /**
     * Notifies that bench card positioned at index1 is attempting to be moved to index2. Assumes
     * that benchIndex1 points to a non-null bench card.
     */

    def benchToBench : (Player, Player, Boolean, Int, Int, Seq[String]) => Option[IntermediaryRequest]

    def request()(req : Option[IntermediaryRequest] = None) : Option[IntermediaryRequest] = req

    /**
     * Notifies that bench card positioned at index is attempting to be moved to active slot. Assumes
     * that benchIndex points to a non-null bench card.
     */
    def benchToActive : (Player, Player, Boolean, Int, Seq[String]) => Option[IntermediaryRequest]

    /**
     * Notifies that active card is attempting to be moved to bench card position at index. Assumes
     * active card is non-null.
     */
    def activeToBench : (Player, Player, Boolean, Int, Seq[String]) => Option[IntermediaryRequest]

    /**
     * Notifies that a card from the hand is attempting to be moved to the active slot. Assumes the hand
     * card exists.
     */
    def handToActive : (Player, Player, Boolean, Int, Seq[String]) => Option[IntermediaryRequest]

    /**
     * Notiies that a card from the hand is attempting to be moved to bench card positioned at index.
     * Assumes the hand card exists.
     */
    def handToBench : (Player, Player, Boolean, Int, Int, Seq[String]) => Option[IntermediaryRequest]

}
