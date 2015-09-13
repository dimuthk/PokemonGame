package src.board.move

import src.board.intermediary.IntermediaryRequest
import src.card.Card
import src.move.Move
import src.card.energy.EnergyCard
import src.card.pokemon.PokemonCard
import src.board.drag._
import src.player.Player

abstract class MoveInterpreter {

    // owner, opp, whether is was owner's card, moveNum, args
    /*def attackFromActiveAdditional : (Player, Player, Boolean, Int, Seq[String]) => Option[IntermediaryRequest]
        = (_, _, _, _, _) => None

    def attackFromActive : (Player, Player, Boolean, Int, Seq[String]) => Unit

    // owner, opp, benchIndex, moveNum, args
    def attackFromBenchAdditional : (Player, Player, Boolean, Int, Int, Seq[String]) => Option[IntermediaryRequest]
        = (_, _, _, _, _, _) => None

    def attackFromBench : (Player, Player, Boolean, Int, Int, Seq[String]) => Unit

    // owner, opp, handIndex, moveNum, args
    def attackFromHandAdditional : (Player, Player, Boolean, Int, Int, Seq[String]) => Option[IntermediaryRequest]
        = (_, _, _, _, _, _) => None

    def attackFromHand : (Player, Player, Boolean, Int, Int, Seq[String]) => Unit

    // owner, opp, moveNum, args
    def attackFromDeckAdditional : (Player, Player, Boolean, Int, Seq[String]) => Option[IntermediaryRequest]
        = (_, _, _, _, _) => None

    def attackFromDeck : (Player, Player, Boolean, Int, Seq[String]) => Unit

    // owner, opp, prizeIndex, moveNum, args
    def attackFromPrizeAdditional : (Player, Player, Boolean, Int, Int, Seq[String]) => Option[IntermediaryRequest]
        = (_, _, _, _, _, _) => None

    def attackFromPrize : (Player, Player, Boolean, Int, Int, Seq[String]) => Unit*/

	// owner, opp, whether is was owner's card, moveNum, args
	def attackFromActive : (Player, Player, Boolean, Int, Seq[String]) => Option[IntermediaryRequest]

	// owner, opp, benchIndex, moveNum, args
    def attackFromBench : (Player, Player, Boolean, Int, Int, Seq[String]) => Option[IntermediaryRequest]

    // owner, opp, handIndex, moveNum, args
    def attackFromHand : (Player, Player, Boolean, Int, Int, Seq[String]) => Option[IntermediaryRequest]

    // owner, opp, moveNum, args
    def attackFromDeck : (Player, Player, Boolean, Int, Seq[String]) => Option[IntermediaryRequest]

    // owner, opp, prizeIndex, moveNum, args
    def attackFromPrize : (Player, Player, Boolean, Int, Int, Seq[String]) => Option[IntermediaryRequest]

    def flipTurn(owner : Player, opp : Player) : Unit = {
        owner.isTurn = !owner.isTurn
        opp.isTurn = !opp.isTurn
    }

}
