package src.move.interceptor

import src.player.Player
import src.card.pokemon.PokemonCard

/**
 * Intercepts a click made by the player
 * how to interpret the operation and what to send back to the players.
 * TODO: work on this one later.
 */
trait ClickInterceptor {

	var isActive : Boolean

	def intercept(owner : Player, opp : Player, attacker : PokemonCard) : Unit
}