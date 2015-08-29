package src.board.move

import src.move.ActivePokemonPower
import src.board.intermediary.IntermediaryRequest
import src.card.Card
import src.move.Move
import src.card.energy.EnergyCard
import src.card.pokemon.PokemonCard
import src.board.drag._
import src.player.Player

abstract class CustomMoveInterpreter extends MoveInterpreter {

    var isActive : Boolean = false

}

/**
 * Prevents all damage done to the Pokemon. Everything else happens.
 * Assumes that the activator of this interpreter is the active defending
 * pokemon.
 */
class PreventDamageInterpreter extends CustomMoveInterpreter {

	override def attack(attacker : Player, defender : Player, m : Move) = m match {
		case ap : ActivePokemonPower => DefaultMoveInterpreter.attack(attacker, defender, m)
		case _ => {
			val currPc = defender.active.get
			val prevHp = currPc.currHp
			DefaultMoveInterpreter.attack(attacker, defender, m)
			if (currPc.currHp < prevHp) {
				defender.notify(currPc.displayName + " prevented the attack!")
				currPc.currHp = prevHp
			}
			currPc.generalCondition = None
			isActive = false
		}
	}

}
