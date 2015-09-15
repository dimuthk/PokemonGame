package src.board.update

import src.board.intermediary.ClickableCardRequest
import src.board.Interpreter
import src.card.pokemon.PokemonCard
import src.player.Player
import src.move._
import src.board.intermediary.IntermediaryRequest

abstract class UpdateInterpreter extends Interpreter[UpdateCommand]

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
object DefaultUpdateInterpreter extends UpdateInterpreter {

  class NextActiveSpecification(p : Player) extends ClickableCardRequest(
        "Select new active",
        "Select your new active pokemon.",
        p,
        1,
        p.bench.toList.flatten)

	override def requestAdditional = (pData, updateCmd, args) => args.length match {
    case 0 => (pData.owner.active.get.currHp, pData.opp.active.get.currHp) match {
      case (0, _) => Some(new NextActiveSpecification(pData.owner))
      case (_, 0) => Some(new NextActiveSpecification(pData.opp))
      case _ => None
    }
    case 1 => (pData.owner.active.get.currHp, pData.opp.active.get.currHp) match {
      // Special case where both actives were knocked out, opp also needs to select now.
      case (0, 0) => Some(new NextActiveSpecification(pData.opp))
      case _ => None
    }
    case _ => None
  }

	/**
   * @param owner References the player who made the current server request.
   *     The turn counter may or may not have flipped afterwards.
   */
  override def handleCommand = (pData, updateCmd, args) => {
    if (pData.owner.performedMove) {
      updateMoveStatuses(pData.owner, pData.opp, false)
    } else {
      updateMoveStatuses(pData.opp, pData.owner, true)
      updateCards(pData.owner, pData.opp)
    }
    cleanBattleground(pData.owner, pData.opp, args)
    if (pData.owner.prizesToAward == 0) {
      pData.owner.performedMove = false
      flipTurn(pData.owner, pData.opp)
    }
  }

  def flipTurn(owner : Player, opp : Player) : Unit = {
    owner.flipTurn()
    opp.flipTurn()
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

  def cleanBattleground(owner : Player, opp : Player, args : Seq[String]) : Unit = {
    if (owner.active.isDefined && owner.active.get.currHp == 0) {
      owner.discardCards(owner.pickUpCard(owner.active.get))
      owner.swapActiveAndBench(args(0).toInt)
      opp.prizesToAward += 1
    }
    for (bc : PokemonCard <- owner.bench.toList.flatten.filter(_.currHp == 0)) {
      owner.discardCards(owner.pickUpCard(bc))
      opp.prizesToAward += 1
    }
    if (opp.active.isDefined && opp.active.get.currHp == 0) {
      opp.discardCards(opp.pickUpCard(opp.active.get))
      opp.swapActiveAndBench(args(if (args.length == 1) 0 else 1).toInt)
      owner.prizesToAward += 1
    }
    for (bc : PokemonCard <- opp.bench.toList.flatten.filter(_.currHp == 0)) {
      opp.discardCards(opp.pickUpCard(bc))
      owner.prizesToAward += 1
    }
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