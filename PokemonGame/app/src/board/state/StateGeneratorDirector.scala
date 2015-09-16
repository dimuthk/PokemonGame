package src.board.state

import play.api.libs.json._
import src.player.Player
import src.card.pokemon.PokemonCard
import src.move._

object StateGeneratorDirector {

	def generateState(p1 : Player, p2: Player, p1IsOwner : Boolean) : ((JsObject, JsObject), (JsObject, JsObject)) = {
		val generator = selectGenerator(p1, p2)
		return (generator.generateForPlayer1(p1, p2),
			generator.generateForPlayer2(p1, p2))
	}

  def selectGenerator(owner : Player, opp : Player) : StateGenerator = {
    for (pc : PokemonCard <- (owner.existingActiveAndBenchCards ++ opp.existingActiveAndBenchCards)) {
      for (m : Move <- pc.existingMoves) {
        if (m.stateGenerator.isDefined && m.stateGenerator.get.isActive) {
          return m.stateGenerator.get
        }
      }
    }
    return DefaultStateGenerator
  }

}