package src.move

import src.board.state.CustomStateGenerator
import src.board.drag.CustomDragInterpreter
import src.move.interceptor._
import src.player.Player

/**
 * Updates the states of each move at the conclusion of a given operation. 
 */
object MoveStatusUpdater {

 /* def updateStatuses(p1 : Player, p2 : Player) : Unit = {
    for (p : Player <- List[Player](p1, p2)) {

    }

  }

  private def updateActive()

  def updateMoveStatuses() {
    for (p : Player <- List[Player](c1.get.p, c2.get.p)) {
      if (p.active.isDefined) {
        val active = p.active.get
        for (m <- List(p.active.get.firstMove, p.active.get.secondMove)) {
          if (m.isDefined) {
              m.get match {
                case power : PokemonPower => {
                  if (active.statusCondition.isEmpty) {
                    if (power.isActivatable) {
                      power.status = if (power.activated) Status.ACTIVATED else Status.ACTIVATABLE
                    } else {
                      power.status = Status.PASSIVE
                    }
                  } else {
                    power.status = Status.DISABLED
                  }
                }
                 // Active pokemon with non-activatable moves should be enabled if there's enough energy.
                case move : Move => move.status =
                  if (move.hasEnoughEnergy(p, active.energyCards)) Status.ENABLED else Status.DISABLED
              }
          }
        }
      }

      for (pc : Option[PokemonCard] <- p.bench) {
        if (pc.isDefined) {
          for (m <- List[Option[Move]](pc.get.firstMove, pc.get.secondMove)) {
            if (m.isDefined) {
              m.get match {
                  case power : PokemonPower => power.status = Status.PASSIVE
                  case move : Move => move.status = Status.DISABLED
              }
            }
          }
        }
      }
    }
  }*/

}
