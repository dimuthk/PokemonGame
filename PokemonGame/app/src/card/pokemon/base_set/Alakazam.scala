package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.move.ActivePokemonPower
import src.board.intermediary.IntermediaryRequest
import src.board.drag.CustomDragInterpreter
import src.board.state.CustomStateGenerator
import src.player.Player
import src.card.energy.EnergyCard
import src.card.energy.EnergyType
import src.card.CardUI
import src.card.CardUI._
import src.card.pokemon._
import src.card.Deck
import play.api.libs.json._
import play.api.Logger

class Alakazam extends BasicPokemon(
    "Alakazam",
    "Alakazam-Base-Set-1.jpg",
    Deck.BASE_SET,
    Identifier.ALAKAZAM,
    id = 65,
    maxHp = 80,
    firstMove = Some(new ActivePokemonPower(
      "Damage Swap",
      dragInterpreter = Some(new DamageSwapDrag()),
      stateGenerator = Some(new DamageSwapState())) {
        def perform = (owner, opp, args) => togglePower()
    }
    ),
    secondMove = Some(new Move(
      "Confuse Ray",
      3,
      Map(EnergyType.PSYCHIC -> 3)) {
      def perform = (owner, opp, args) => confuseAttackChance(owner, opp, 30)
    }),
    energyType = EnergyType.PSYCHIC,
    weakness = Some(EnergyType.PSYCHIC),
    retreatCost = 3)


private class DamageSwapState extends CustomStateGenerator(true, false) {

  override def generateForOwner = (owner, opp, interceptor) => {
    // Active and bench cards are visible and draggable to allow energy transfer.
    owner.setUIOrientationForActiveAndBench(Set(FACE_UP, DRAGGABLE))
    // Hand is deactivated.
    owner.setUiOrientationForHand(Set())

    // Opponent active and bench cards are visible but not clickable.
    opp.setUIOrientationForActiveAndBench(Set(FACE_UP))
    opp.setUiOrientationForHand(Set())

    // Venusaur must still be usable to deactivate power.
    owner.setUIOrientationForActiveAndBench(Set(FACE_UP, CLICKABLE, DISPLAYABLE, DRAGGABLE, USABLE))
    (owner.toJson, opp.toJson)
  }

}

private class DamageSwapDrag extends CustomDragInterpreter {

  def benchToBench = (p, _, _, benchIndex1, benchIndex2, _) => {
    if (p.bench(benchIndex2).isDefined) {
      swapDamage(p.bench(benchIndex1).get, p.bench(benchIndex2).get)
    }
    None
  }

  def benchToActive = (p, _, _, benchIndex, _) => {
    if (p.active.isDefined) {
      swapDamage(p.bench(benchIndex).get, p.active.get)
    }
    None
  }

  def activeToBench = (p, _, _, benchIndex, _) => {
    if (p.bench(benchIndex).isDefined) {
      swapDamage(p.active.get, p.bench(benchIndex).get)
    }
    None
  }

  def handToActive = (_, _, _, _, _) => None

  def handToBench = (_, _, _, _, _, _) => None

  private def swapDamage(drag : PokemonCard, drop : PokemonCard) : Unit = {
    if (drop.currHp > 10 && drag.currHp < drag.maxHp) {
      drag.heal(10)
      drop.takeDamage(None, 10)
    }
  }

}

