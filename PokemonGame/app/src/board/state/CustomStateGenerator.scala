package src.board.state

import src.card.pokemon.PokemonCard
import src.player.Player
import play.api.libs.json._

/**
 * A custom generator which can be implemented by outside resources. Custom
 * generations are hinged on a separate class, belonging to one of the players,
 * and thus have no concept of absolute board orientation. This class will
 * bind the absolute orientation to the relative one which can be interpreted
 * by the hinge class.
 */
abstract class CustomStateGenerator(
  val willOverrideOwnerState : Boolean,
  val willOverrideOppState : Boolean) {

  final def generateForPlayer1(p1 : Player, p2: Player, hinge : PokemonCard) : (JsObject, JsObject) = {
  	if (cardBelongsToPlayer(hinge, p1)) {
      if (willOverrideOwnerState) {
        return generateForOwner(p1, p2, hinge)
      } else {
        return DefaultStateGenerator.generateForPlayer1(p1, p2)
      }

  	} else {
      if (willOverrideOppState) {
        return generateForOpp(p1, p2, hinge)
      } else {
        return DefaultStateGenerator.generateForPlayer1(p1, p2)
      }
  	}
  }

  final def generateForPlayer2(p1 : Player, p2: Player, hinge : PokemonCard) : (JsObject, JsObject) = {
  	if (cardBelongsToPlayer(hinge, p2)) {
      if (willOverrideOwnerState) {
        return generateForOwner(p2, p1, hinge)
      } else {
        return DefaultStateGenerator.generateForPlayer2(p1, p2)
      }
  	} else {
      if (willOverrideOppState) {
        return generateForOpp(p2, p1, hinge)
        } else {
          return DefaultStateGenerator.generateForPlayer2(p1, p2)
        }
  	}
  }

  private def cardBelongsToPlayer(pc : PokemonCard, p : Player) : Boolean = {
  	for (oc : Option[PokemonCard] <- p.bench ++ List(p.active)) {
  		if (oc.isDefined && oc.get == pc) {
  			return true
  		}
  	}
  	return false
  }

  var isActive : Boolean = false

  def generateForOwner(owner : Player, opp : Player, hinge : PokemonCard) : (JsObject, JsObject) = null

  def generateForOpp(opp : Player, owner : Player, hinge : PokemonCard) : (JsObject, JsObject) = null
}