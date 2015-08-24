package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.interceptor._
import src.move.MoveBuilder._
import src.move.PokemonPower
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

class Venusaur extends PokemonCard(
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
    retreatCost = 2,
    evolutionStage = EvolutionStage.BASIC) {

  override def isEvolutionOf(pokemon : PokemonCard) = pokemon.id == 2

}

private class EnergyTransState extends CustomStateGenerator {

  override def willIntercept(owner : Player, opp : Player) = true

  override def generateForOwner(owner : Player, opp : Player, interceptor : PokemonCard) : (JsObject, JsObject) = {
    // Active and bench cards are visible and draggable to allow energy transfer.
    owner.setUIOrientationForActiveAndBench(Set(FACE_UP, DRAGGABLE))
    // Hand is deactivated.
    owner.setUiOrientationForHand(Set())

    // Opponent active and bench cards are visible but not clickable.
    opp.setUIOrientationForActiveAndBench(Set(FACE_UP))
    opp.setUiOrientationForHand(Set())

    // Venusaur must still be usable to deactivate power.
    owner.setUIOrientationForActiveAndBench(Set(FACE_UP, CLICKABLE, DRAGGABLE, USABLE))
    return (owner.toJson, opp.toJson)
  }

  override def generateForOpp(opp : Player, owner : Player, interceptor : PokemonCard) : (JsObject, JsObject) = {
    // Cards are visible and nothing else.
    opp.setUIOrientationForActiveAndBench(Set(FACE_UP))
    opp.setUiOrientationForHand(Set(FACE_UP))

    owner.setUIOrientationForActiveAndBench(Set(FACE_UP))
    owner.setUiOrientationForHand(Set())
    return (opp.toJson, owner.toJson)
  }

}

private class EnergyTrans extends PokemonPower(
  "Energy Trans",
  isActivatable = true,
  stateGenerator = Some(new EnergyTransState())) {

  override def perform(owner : Player, opp : Player) : Unit = {
    activated = !activated
    stateGenerator.get.isActive = !stateGenerator.get.isActive
  }

 /* override def handleMove(owner : Player, opp : Player, moveName : String, itemMap : Map[String, Int]) {
    moveName match {
      case "BENCH_TO_BENCH" => {
        val benchIndex2 = itemMap.getOrElse("drop", -1)
        if (owner.bench(benchIndex2).isEmpty) {
          return
        }
        val benchIndex1 = itemMap.getOrElse("drag", -1)
        val dragTarget = owner.bench(benchIndex1).get
        val dropTarget = owner.bench(benchIndex2).get
        transferLeafEnergy(dragTarget, dropTarget)
      }
      case "BENCH_TO_ACTIVE" => {
        if (owner.active.isEmpty) {
          return
        }
        val benchIndex = itemMap.getOrElse("drag", -1)
        transferLeafEnergy(owner.bench(benchIndex).get, owner.active.get)
      }
      case "ACTIVE_TO_BENCH" => {
        val benchIndex = itemMap.getOrElse("drop", -1)
        if (owner.bench(benchIndex).isEmpty) {
          return
        }
        transferLeafEnergy(owner.active.get, owner.bench(benchIndex).get)
      }
      case _ => () 
    }
    
  }

  def transferLeafEnergy(drag : PokemonCard, drop : PokemonCard) {
    for (i <- 0 until drag.energyCards.length) {
      if (drag.energyCards(i).eType == EnergyType.GRASS) {
        val ec : EnergyCard = drag.energyCards(i)
        drop.energyCards = drop.energyCards ++ List(ec)
        drag.energyCards = drag.energyCards.slice(0, i) ++ drag.energyCards.slice(i + 1, drag.energyCards.size)
        return
      }
    }
  }*/

}

private class Solarbeam extends Move(
  "Solarbeam",
  4,
  Map(EnergyType.GRASS -> 4)) {

  override def perform(owner : Player, opp : Player) = standardAttack(owner, opp, 60)
}
