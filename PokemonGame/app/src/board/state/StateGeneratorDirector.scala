package src.board.state

import play.api.libs.json._
import src.player.Player
import src.card.pokemon.PokemonCard
import src.move._

object StateGeneratorDirector {


	def generateState(p1 : Player, p2: Player, p1IsOwner : Boolean) : ((JsObject, JsObject), (JsObject, JsObject)) = {
		val ui = interceptedUI(p1, p2, if (p1IsOwner) p1 else p2)
		if (ui.isDefined) {
			return ui.get
		}
		return (DefaultStateGenerator.generateForPlayer1(p1, p2),
			DefaultStateGenerator.generateForPlayer2(p1, p2))
	}

	def interceptedUI(p1 : Player, p2 : Player, owner : Player) : Option[((JsObject, JsObject), (JsObject, JsObject))] = {
      for (pc : Option[PokemonCard] <- owner.bench ++ List(owner.active)) {
        if (pc.isDefined) {
          for (om : Option[Move] <- List(pc.get.firstMove, pc.get.secondMove)) {
            if (om.isDefined && om.get.stateGenerator.isDefined) {
              val generator = om.get.stateGenerator.get
              if (generator.isActive) {
                return Some(generator.generateForPlayer1(p1, p2, pc.get), generator.generateForPlayer2(p1, p2, pc.get))
              }
            }
          }
        }
      }
      return None
    }
}