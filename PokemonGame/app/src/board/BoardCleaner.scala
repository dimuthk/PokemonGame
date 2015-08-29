package src.board

import play.api.Logger
import src.card.pokemon.PokemonCard
import src.player.Player
import src.move._

/**
 * Polishes up the board before sending out to the client. This includes updating
 * statuses of moves, and detecting if the active on either side is knocked out,
 * in which case a request is generated for the corresponding player. 
 */
object BoardCleaner {

  /**
   * @param owner References the player who made the current server request.
   *     The turn counter may or may not have flipped afterwards.
   */
	def cleanUpBoardState(owner : Player, opp : Player) {
    if (owner.isTurn) {
      updateBoardOnSameTurn(owner, opp)
    } else {
      Logger.debug("update on turn swap")
      updateBoardOnTurnSwap(owner, opp)
      updateCards(owner : Player, opp : Player)
    }
	}

  def updateCards(owner : Player, opp : Player) {
    if (owner.active.isDefined) {
      owner.active.get.updateCardOnTurnSwap(owner, opp, true)
    }
    if (opp.active.isDefined) {
      opp.active.get.updateCardOnTurnSwap(opp, owner, true)
    }
    for (pc : PokemonCard <- (owner.bench.toList.flatten ++ opp.bench.toList.flatten)) {
      pc.updateCardOnTurnSwap(opp, owner, false)
    }
  }

  /**
   * Process how to update the board after a turn swap. @param owner references
   * the player whose turn just ended.
   */
  private def updateBoardOnTurnSwap(owner : Player, opp : Player) {
    updateMoveStatuses(opp, owner, true)
  }

  private def updateBoardOnSameTurn(owner : Player, opp : Player) {
    updateMoveStatuses(owner, opp, false)
  }


	private def updateMoveStatuses(curr : Player, next : Player, turnSwapped : Boolean) {
    if (curr.active.isDefined) {
      curr.active.get.updateMoves(curr, next, turnSwapped, true)
    }
    if (next.active.isDefined) {
      next.active.get.updateMoves(next, curr, turnSwapped, true)
    }
    for (pc : PokemonCard <- curr.bench.toList.flatten) {
      pc.updateMoves(curr, next, turnSwapped, false)
    }
    for (pc : PokemonCard <- next.bench.toList.flatten) {
      pc.updateMoves(next, curr, turnSwapped, false)
    }
  }

}