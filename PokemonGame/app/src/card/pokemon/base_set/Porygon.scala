package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy._
import src.card.energy.EnergyCard._
import src.board.intermediary.ClickableCardRequest
import src.card.pokemon._
import src.card.Deck

class Porygon extends BasicPokemon(
	"Porygon",
	"Porygon-Base-Set-39.jpg",
	Deck.BASE_SET,
	Identifier.PORYGON,
	id = 137,
	maxHp = 30,
	firstMove = Some(new Move(
		"Conversion 1",
		1) {
			def perform = (owner, opp, args) => args.length match {
				case 0 => Some(new TypeChooseSpecification(owner))
				case _ => {
					val eType = energyList(args(0).toInt).eType
					opp.active.get.currWeakness = Some(eType)
					None
				}
			}
		}),
	secondMove = Some(new Move(
		"Conversion 2",
		2) {
			def perform = (owner, opp, args) => args.length match {
				case 0 => Some(new TypeChooseSpecification(owner))
				case _ => {
					val eType = energyList(args(0).toInt).eType
					owner.active.get.currResistance = Some(eType)
					None
				} 
			}
		}),
	energyType = EnergyType.COLORLESS,
	weakness = Some(EnergyType.FIGHTING),
	resistance = Some(EnergyType.PSYCHIC),
	retreatCost = 1) {
}

class TypeChooseSpecification(p : Player) extends ClickableCardRequest(
        "Select Energy",
        "Choose an energy type to switch to.",
        p,
        1,
        energyList)