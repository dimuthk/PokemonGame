package src.card.pokemon.base_set

import src.board.intermediary.OpponentCardInterface
import src.json.Identifier
import src.move._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck
import play.api.Logger

class Poliwhirl extends StageOnePokemon(
	"Poliwhirl",
	"Poliwhirl-Base-Set-38.jpg",
	Deck.BASE_SET,
	Identifier.POLIWHIRL,
	id = 61,
	maxHp = 60,
	firstMove = Some(new Amnesia()),
	secondMove = Some(new Move(
		"Doubleslap",
		3,
		Map(EnergyType.WATER -> 2)) {
			def perform = (owner, opp, args) => multipleHitAttack(owner, opp, 30, 2)
		}),
	energyType = EnergyType.WATER,
	weakness = Some(EnergyType.GRASS),
	retreatCost = 1)

class Amnesia extends Move(
	"Amnesia",
	2,
	Map(EnergyType.WATER -> 2)) {

	def perform = (owner, opp, args) => args.length match {
		case 0 => {
			opp.active.get.firstMove match {
				case Some(p : PokemonPower) => p.status = Status.DISABLED
				case Some(p : Pass) => p.status = Status.DISABLED
				case Some(m : Move) => m.status = Status.ENABLED
				case _ => ()
			}
			Some(new OpponentCardInterface("Select move", "Select the opponent's move to disable.", owner, opp.active.get))
		}
		case _ => {
			Logger.debug("GOT INFO BACK")
			val moveNum = args(0).toInt
			args(0).toInt match {
				case 1 => opp.active.get.firstMove.get.amnesia = true
				case 2 => opp.active.get.secondMove.get.amnesia = true
			}
			None
		}
	}

}