package src.board.update

import src.board.Interpreter
import src.card.pokemon.PokemonCard
import src.player.Player
import src.move._
import src.board.intermediary.IntermediaryRequest

abstract class UpdateInterpreter extends Interpreter[UpdateCommand]

object DefaultUpdateInterpreter extends UpdateInterpreter {

	override def requestAdditional = (pData, updateCmd, args) => None

	/**
   * @param owner References the player who made the current server request.
   *     The turn counter may or may not have flipped afterwards.
   */
  override def handleCommand = (pData, updateCmd, args) => {
    if (pData.owner.isTurn) {
      updateMovesOnSameTurn(pData.owner, pData.opp)
    } else {
      updateMovesOnTurnSwap(pData.owner, pData.opp)
      updateCards(pData.owner, pData.opp)
    }
    cleanBattleground(pData.owner, pData.opp)
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
   * General flow of events:
   * 1) Count the number of knocked out pokemon on both sides and tally the respective
   * prizes to be awarded. Clean up and discard all of those cards.
   * 2) Use intermediaries to assign a new active for one or both players, if their
   * active got knocked out (so this may require chaining intermediaries). If either
   * is unable to set a new active, conclude the game.
   * 3) If the player who ISN'T going next has prize cards to collect, flip the turns
   * and allow that player to select his cards before flipping back.
   */
  def cleanBattleground(owner : Player, opp : Player) : Option[IntermediaryRequest] = {
    var ownerSelectNewActive : Boolean = false
    if (owner.active.isDefined && owner.active.get.currHp == 0) {
      // Because turn is about to flip, owner must select a new active
      // pokemon before opp gets control.
      owner.discardCards(owner.pickUpCard(owner.active.get))
      ownerSelectNewActive = true
      opp.prizesToAward += 1
    }
    for (bc : PokemonCard <- owner.bench.toList.flatten) {
      if (bc.currHp == 0) {
        owner.discardCards(owner.pickUpCard(bc))
        opp.prizesToAward += 1
      }
    }
    for (pc : PokemonCard <- opp.existingActiveAndBenchCards) {
      if (pc.currHp == 0) {
        opp.discardCards(opp.pickUpCard(pc))
        owner.prizesToAward += 1
      }
    }
    return None
  }

  /**
   * Process how to update the board after a turn swap. @param owner references
   * the player whose turn just ended.
   */
  private def updateMovesOnTurnSwap(owner : Player, opp : Player) {
    updateMoveStatuses(opp, owner, true)
  }

  private def updateMovesOnSameTurn(owner : Player, opp : Player) {
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