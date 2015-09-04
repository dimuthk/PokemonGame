package src.card.pokemon.fossil

import src.card.Deck
import src.card.pokemon._
import src.json.Identifier
import src.move.Move
import src.board.drag.CustomDragInterpreter
import src.board.state.CustomStateGenerator
import src.move.MoveBuilder._
import src.move.ActivePokemonPower
import src.player.Player
import src.card.CardUI
import src.card.CardUI._
import src.card.energy.EnergyCard
import src.card.energy.EnergyType
import src.card.energy.WaterEnergy

class Slowbro extends StageOnePokemon(
    "Slowbro",
    "Slowbro-Fossil-43.jpg",
    Deck.FOSSIL,
    Identifier.SLOWBRO,
    id = 80,
    maxHp = 60,
    firstMove = Some(new ActivePokemonPower(
      "Strange Behavior",
      dragInterpreter = Some(new StrangeBehaviorDrag()),
      stateGenerator = Some(new StrangeBehaviorState())) {
        def perform = (owner, opp, args) => togglePower()
    }),
    secondMove = Some(new Move(
      "Psyshock",
      2,
      Map(EnergyType.PSYCHIC -> 2)) {
        def perform = (owner, opp, args) => paralyzeAttackChance(owner, opp, 20)
      }),
    energyType = EnergyType.PSYCHIC,
    weakness = Some(EnergyType.PSYCHIC),
    retreatCost = 1)

private class StrangeBehaviorState extends CustomStateGenerator(true, false) {

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

private class StrangeBehaviorDrag extends CustomDragInterpreter {

  def benchToBench = (p, _, _, benchIndex1, benchIndex2, _) => {
    val card = p.cardWithActivatedPower
    p.bench(benchIndex2) match {
      case Some(card) => swapDamage(p.bench(benchIndex1).get, card)
      case _ => ()
    }
    None
  }

  def benchToActive = (p, _, _, benchIndex, _) => {
    val card = p.cardWithActivatedPower
    p.active match {
      case Some(card) => swapDamage(p.bench(benchIndex).get, card)
      case _ => ()
    }
    None
  }

  def activeToBench = (p, _, _, benchIndex, _) => {
    val card = p.cardWithActivatedPower
    p.bench(benchIndex) match {
      case Some(card) => swapDamage(p.active.get, card)
      case _ => ()
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