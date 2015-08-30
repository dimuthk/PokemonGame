package src.card.pokemon.base_set

import src.json.Identifier
import src.move._
import src.board.intermediary.IntermediaryRequest
import src.board.intermediary.IntermediaryRequest._
import src.board.intermediary.ClickableCardRequest
import src.move.MoveBuilder._
import src.card.condition.StatusCondition
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck
import play.api.Logger

class Vileplume extends BasicPokemon(
	"Vileplume",
	"Vileplume-Jungle-15.jpg",
	Deck.JUNGLE,
	Identifier.VILEPLUME,
	id = 45,
	maxHp = 80,
	firstMove = Some(new Heal()),
	secondMove = Some(new Move(
		"Petal Dance",
        3,
        Map(EnergyType.GRASS -> 3)) {
			def perform = (owner, opp) => {
        multipleHitAttack(owner, opp, 40, 3)
				owner.active.get.statusCondition = Some(StatusCondition.CONFUSED)
			}
        }),
	energyType = EnergyType.GRASS,
	weakness = Some(EnergyType.FIRE),
	retreatCost = 1) {


}

private class Heal extends ActivePokemonPower("Heal") {

	class HealChooseSpecification(
        p : Player,
        benchAndActiveCards : Seq[PokemonCard]) extends ClickableCardRequest(
        "Select Pokemon",
        "Choose a pokemon to heal.",
        "MOVE<>ATTACK_FROM_ACTIVE<>1<>",
        p,
        1,
        benchAndActiveCards)

  var usedHeal : Boolean = false

  override def update(owner : Player, opp : Player, pc : PokemonCard, turnSwapped : Boolean, isActive : Boolean) : Unit = {
    super.update(owner, opp, pc, turnSwapped, isActive)
    if (turnSwapped && owner.isTurn) {
      usedHeal = false
    }
    if (usedHeal) {
      status = Status.DISABLED
    }
  }

	override def perform = (owner, opp) => ()

  	override def additionalRequest(owner : Player, opp : Player, args : Seq[String]) : Option[IntermediaryRequest] = usedHeal match {
  		case true => None
  		case false => {
  			usedHeal = true
  			return flippedHeads() match {
  				case true => Some(new HealChooseSpecification(owner, owner.existingActiveAndBenchCards))
  				case false => None
  			}
  		}
  	}

  	override def performWithAdditional(owner : Player, opp : Player, args : Seq[String]) {
  		var rawIndex = args.head.toInt
  		if (owner.active.isDefined) {
  			if (rawIndex == 0) {
  				owner.active.get.heal(10)
          return
  			} else {
  				rawIndex = rawIndex - 1
  			}
  		}
  		val benchIndex = getRealIndexFor(rawIndex, owner.bench)
  		owner.bench(benchIndex).get.heal(10)
  	}
}