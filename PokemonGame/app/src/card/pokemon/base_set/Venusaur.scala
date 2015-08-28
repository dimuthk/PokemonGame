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

class Venusaur extends StageTwoPokemon(
    "Venusaur",
    "Venusaur-Base-Set-15.jpg",
    Deck.BASE_SET,
    Identifier.VENUSAUR,
    id = 3,
    maxHp = 100,
    firstMove = Some(new EnergyTrans()),
    secondMove = Some(new Solarbeam()),
    energyType = EnergyType.GRASS,
    weakness = Some(EnergyType.FIRE),
    retreatCost = 2)

private class EnergyTrans extends ActivePokemonPower(
  "Energy Trans",
  dragInterpreter = Some(new EnergyTransDrag()),
  stateGenerator = Some(new EnergyTransState()))


private class Solarbeam extends Move(
  "Solarbeam",
  4,
  Map(EnergyType.GRASS -> 4)) {

  override def perform(owner : Player, opp : Player) = standardAttack(owner, opp, 60)
}

private class EnergyTransState extends CustomStateGenerator(true, false) {

  override def generateForOwner(owner : Player, opp : Player, interceptor : PokemonCard) : (JsObject, JsObject) = {
    // Active and bench cards are visible and draggable to allow energy transfer.
    owner.setUIOrientationForActiveAndBench(Set(FACE_UP, DRAGGABLE))
    // Hand is deactivated.
    owner.setUiOrientationForHand(Set())

    // Opponent active and bench cards are visible but not clickable.
    opp.setUIOrientationForActiveAndBench(Set(FACE_UP))
    opp.setUiOrientationForHand(Set())

    // Venusaur must still be usable to deactivate power.
    owner.setUIOrientationForActiveAndBench(Set(FACE_UP, CLICKABLE, DISPLAYABLE, DRAGGABLE, USABLE))
    return (owner.toJson, opp.toJson)
  }

}

private class EnergyTransDrag extends CustomDragInterpreter {

  override def benchToBench(p : Player, benchIndex1 : Int, benchIndex2 : Int) : Unit = {
    if (p.bench(benchIndex2).isDefined) {
      transferLeafEnergy(p.bench(benchIndex1).get, p.bench(benchIndex2).get)
    }
  }

  override def benchToActive(p : Player, benchIndex : Int) : Unit = {
    if (p.active.isDefined) {
      transferLeafEnergy(p.bench(benchIndex).get, p.active.get)
    }
  }

  override def activeToBench(p : Player, benchIndex : Int) : Unit = {
    if (p.bench(benchIndex).isDefined) {
      transferLeafEnergy(p.active.get, p.bench(benchIndex).get)
    }
  }

  override def handToActive(p : Player, handIndex : Int) : Unit = ()

  override def handToBench(p : Player, handIndex : Int, benchIndex : Int) : Unit = ()

  private def transferLeafEnergy(drag : PokemonCard, drop : PokemonCard) {
    for (i <- 0 until drag.energyCards.length) {
      if (drag.energyCards(i).eType == EnergyType.GRASS) {
        val ec : EnergyCard = drag.energyCards(i)
        drop.energyCards = drop.energyCards ++ List(ec)
        drag.energyCards = drag.energyCards.slice(0, i) ++ drag.energyCards.slice(i + 1, drag.energyCards.size)
        return
      }
    }
  }

}

