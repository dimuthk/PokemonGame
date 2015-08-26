package src.board

import src.card.pokemon.PokemonCard
import src.player.Player
import src.move._

/**
 * Polishes up the board before sending out to the client. This includes updating
 * statuses of moves, and detecting if the active on either side is knocked out,
 * in which case a request is generated for the corresponding player.
 */
object BoardCleaner {

	def cleanUpBoardState(owner : Player, opp : Player) {
		updateMoveStatuses(owner, opp)
	}

	def updateMoveStatuses(owner : Player, opp : Player) {
    	for (p : Player <- List[Player](owner, opp)) {
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
    }

}