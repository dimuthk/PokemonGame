package src.board.drag

import play.api.Logger
import src.board.intermediary.ClickableCardRequest
import src.board.intermediary.IntermediaryRequest
import src.card.Card
import src.card.energy.EnergyCard
import src.card.pokemon._
import src.card.pokemon.jungle.Dodrio
import src.board.drag._
import src.player.Player

class RetreatEnergySpecification(
  p : Player, clickCount : Int, energyCards : Seq[EnergyCard], benchIndex : Int) extends ClickableCardRequest(
  "Discard Energy",
  "Select the energy cards you want to discard.",
  p,
  clickCount,
  energyCards,
  additionalTag = Some(benchIndex + ""))

object DefaultDragInterpreter extends DragInterpreter {

  val restrictEnergyAttach = false

  /*override def additionalRequest(p : Player, cmd : DragCommand) : Option[IntermediaryRequest] = cmd match {
    case BenchToActive(benchIndex : Int) => checkActiveRestoreAmbiguity(p, p.active, benchIndex)
    case ActiveToBench(benchIndex : Int) => checkActiveRestoreAmbiguity(p, p.active, benchIndex)
    case _ => None
  }*/

  private def retreatCost(owner : Player) : Int = {
    val numDodrios = owner.bench.toList.flatten.filter{
      case d : Dodrio => true
      case _ => false
      }.length
    return math.max(owner.active.get.retreatCost - numDodrios, 0)
  }

  private def checkActiveRestoreAmbiguity(p : Player, active : Option[PokemonCard], benchIndex : Int): Option[RetreatEnergySpecification] = {
    if (active.isDefined && retreatCost(p) > 0 && active.get.energyCards.length > 1) {
      if (hasMultipleEnergyTypes(active.get.energyCards)) {
        return Some(new RetreatEnergySpecification(p, retreatCost(p), active.get.energyCards, benchIndex))
      }
    }
    return None
  }

  private def hasMultipleEnergyTypes(energyCards : Seq[EnergyCard]) : Boolean = {
    if (energyCards.length > 1) {
      val firstCard = energyCards(0)
      return !energyCards.filter(_.eType != firstCard.eType).isEmpty
    }
    return false;
  }

  private def attachEnergyToPokemon(p : Player, ec : EnergyCard, handIndex : Int, pc : PokemonCard) {
    if (!p.addedEnergy || !restrictEnergyAttach) {
      pc.energyCards = pc.energyCards ++ List(ec)
      p.removeCardFromHand(handIndex)
      p.addedEnergy = true
    }
  }

  /**
   * Corresponds to a specification on which energy cards to discard when retreating an active pokemon
   * to the bench.
   */
  /*override def handleIntermediary(p : Player, cmd : Seq[String]) {
    val benchIndex : Int = cmd(0).toInt
    val eCardIndices : Seq[Int] = cmd(1).split(",").map(_.toInt)
    val active = p.active.get
    p.swapActiveAndBench(benchIndex)
    p.bench(benchIndex).get.energyCards = p.bench(benchIndex).get.energyCards
      .zipWithIndex
      .filterNot { case (_, index) => eCardIndices.contains(index) }
      .map(_._1)
  }*/

  def benchToBench = (p, _, _, benchIndex1, benchIndex2, _) => {
    p.swapBenchCards(benchIndex1, benchIndex2)
    None
  }

  def benchToActive = (p, _, _, benchIndex, _) => {
    if (p.active.isDefined) {
      if (chargeRetreat(p, benchIndex)) {
        p.swapActiveAndBench(benchIndex)
      }
    } else  {
      p.swapActiveAndBench(benchIndex)
    }
    None
  }

  def activeToBench = (p, _, _, benchIndex, _) => {
    if (chargeRetreat(p, benchIndex)) {
      p.swapActiveAndBench(benchIndex)
    }
    None
  }

    private def chargeRetreat(p : Player, benchIndex : Int) : Boolean = {
      val active = p.active.get
      if (active.getTotalEnergy() >= retreatCost(p)) {
        active.energyCards = active.energyCards.dropRight(retreatCost(p))
        return true
      } else {
        return false
      }
    }

  def handToActive = (p, _, _, handIndex, _) => {
    (p.hand(handIndex), p.active) match {
      // Moving basic pokemon from hand to active slot.
      case (bp : BasicPokemon, None) => p.moveHandToActive(handIndex)
      // Attaching energy card to active pokemon.
      case (ec : EnergyCard, Some(active)) => attachEnergyToPokemon(p, ec, handIndex, active)
      // Evolving active pokemon.
      case (ep : EvolvedPokemon, Some(active)) => p.evolveActiveCard(handIndex)
      case _ => ()
    }
    None
  }

  def handToBench = (p, _, _, handIndex, benchIndex, _) => {
    (p.hand(handIndex), p.bench(benchIndex)) match {
      // Moving basic pokemon to empty bench slot.
      case (bp : BasicPokemon, None) => p.moveHandToBench(handIndex, benchIndex)
      // Attaching energy card to bench pokemon.
      case (ec : EnergyCard, Some(bc)) => attachEnergyToPokemon(p, ec, handIndex, bc)
      // Evolving this bench pokemon.
      case (ep : EvolvedPokemon, Some(bc)) => p.evolveBenchCard(handIndex, benchIndex)
      case _ => ()
    }
    None
  }

}
