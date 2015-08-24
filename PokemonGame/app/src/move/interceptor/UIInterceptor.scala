package src.move.interceptor

import src.board.drag.DragCommand
import src.player.Player
import src.card.pokemon.PokemonCard
import play.api.libs.json._


/**
 * Intercepts the UI operation to be broadcasted to the players.
 */
trait UIInterceptor {

	var isActive : Boolean = false

	def willIntercept(owner : Player, opp : Player) : Boolean

	def orientationForOwner(owner : Player, opp : Player, interceptor : PokemonCard) : (JsObject, JsObject)

	def orientationForOpp(owner : Player, opp : Player, interceptor : PokemonCard) : (JsObject, JsObject)
}