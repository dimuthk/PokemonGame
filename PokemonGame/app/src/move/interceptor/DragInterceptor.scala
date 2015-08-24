package src.move.interceptor

import src.board.drag.DragCommand
import src.player.Player

/**
 * Intercepts a drag operation made by a player and handles
 * how to interpret the operation and what to send back tot he players.
 */
trait DragInterceptor {

	var isActive : Boolean

	def willIntercept(owner : Player, opp : Player, cmd : DragCommand) : Boolean

	def intercept(owner : Player, opp : Player, cmd : DragCommand) : Unit
}