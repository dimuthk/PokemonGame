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
      updateBoardOnTurnSwap(owner, opp)
    }
	}

  /**
   * Process how to update the board after a turn swap. @param owner references
   * the player whose turn just ended.
   */
  private def updateBoardOnTurnSwap(owner : Player, opp : Player) {
    updateMoveStatuses(opp, owner)
  }

  private def updateBoardOnSameTurn(owner : Player, opp : Player) {
    updateMoveStatuses(owner, opp)
  }

  private def updateBenchCardMoves(curr : Player, next : Player) {
    for (bc : PokemonCard <- curr.bench.toList.flatten) {
      for (m : Move <- bc.getExistingMoves()) {
        m match {
          case ap : ActivePokemonPower => handleActivePowerStatus(curr, bc, ap)
          case pp : PassivePokemonPower => handlePassivePowerStatus(bc, pp)
          case _ => {
            m.status = Status.DISABLED
          }
        }
      }
    }
    for (bc : PokemonCard <- next.bench.toList.flatten) {
      for(m : Move <- bc.getExistingMoves()) {
        m match {
          case pp : PassivePokemonPower => handlePassivePowerStatus(bc, pp)
          case _ => {
            m.status = Status.DISABLED
          }
        }
      }
    }
  }

  private def handleActivePowerStatus(p : Player, pc : PokemonCard, ap : ActivePokemonPower) {
    if (!pc.statusCondition.isEmpty) {
      ap.status = Status.DISABLED
    } else {
      val activatedCard : Option[PokemonCard] = cardWithActivatedPower(p)
      if (activatedCard.isDefined && activatedCard.get != pc) {
        ap.status = Status.DISABLED
      } else {
        ap.status = if (ap.isActive) Status.ACTIVATED else Status.ACTIVATABLE
      }
    }
  }

  private def handlePassivePowerStatus(pc : PokemonCard, pp : PassivePokemonPower) {
    if (!pc.statusCondition.isEmpty) {
      pp.status = Status.DISABLED
    } else {
      pp.status = Status.PASSIVE
    }
  }

  private def cardWithActivatedPower(p : Player) : Option[PokemonCard] = {
    for (pc : PokemonCard <- p.getExistingActiveAndBenchCards()) {
      for (m : Move <- pc.getExistingMoves()) {
        m match {
          case ap : ActivePokemonPower => {
            if (ap.isActive) {
              return Some(pc)
            }
          }
          case _ => ()
        }
      }
    }
    return None
  }

  private def updateActiveCardMoves(curr : Player, next : Player) {
    if (curr.active.isDefined) {
      val active = curr.active.get
      for (m : Move <- active.getExistingMoves()) {
        m match {
          case ap : ActivePokemonPower => handleActivePowerStatus(curr, active, ap)
          case pp : PassivePokemonPower => handlePassivePowerStatus(active, pp)
          case _ => {
            if (cardWithActivatedPower(curr) != None) {
              m.status = Status.DISABLED
            } else {
              m.status = if (m.hasEnoughEnergy(curr, active.energyCards)) Status.ENABLED else Status.DISABLED
            }
          }
        }
      }
    }
    if (next.active.isDefined) {
      val active = next.active.get
      for (m : Move <- active.getExistingMoves()) {
        m match {
          case pp : PassivePokemonPower => handlePassivePowerStatus(active, pp)
          case _ => {
            m.status = Status.DISABLED
          }
        }
      }
    }
  }

	private def updateMoveStatuses(curr : Player, next : Player) {
    updateActiveCardMoves(curr, next)
    updateBenchCardMoves(curr, next)
  }

}