package src.move.interceptor

import src.player.Player
import src.card.pokemon.PokemonCard

/**
 * Intercepts a move performed by a player
 * how to interpret the operation and what to send back to the players.
 */
trait MoveInterceptor {

	var isActive : Boolean

	def intercept(owner : Player, opp : Player, attacker : PokemonCard) : Unit
}