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
  p : Player, clickCount : Int, energyCards : Seq[EnergyCard]) extends ClickableCardRequest(
  "Discard Energy",
  "Select the energy cards you want to discard.",
  p,
  clickCount,
  energyCards)

object DefaultDragInterpreter extends DragInterpreter {

  val restrictEnergyAttach = false

  private def retreatCost(owner : Player) : Int = {
    val numDodrios = owner.bench.toList.flatten.filter{
      case d : Dodrio => true
      case _ => false
    }.length
    return math.max(owner.active.get.retreatCost - numDodrios, 0)
  }

  private def checkForAmbiguity(p : Player): Option[RetreatEnergySpecification] = p.active match {
    case Some(active) => {
      if (retreatCost(p) > 0
        && active.energyCards.length >= retreatCost(p)
        && hasMultipleEnergyTypes(active.energyCards)) {
        return Some(new RetreatEnergySpecification(p, retreatCost(p), active.energyCards))
      } else {
        return None
      }
    }
    case None => None
  }

  private def hasMultipleEnergyTypes(energyCards : Seq[EnergyCard]) : Boolean = {
    if (energyCards.length > 1) {
      val firstCard = energyCards(0)
      return !energyCards.filter(_.eType != firstCard.eType).isEmpty
    }
    return false;
  }

  /**
   * Assumes there are no ambiguities in energy types. If card has enough energy to retreat, will charge
   * retreat cost and swap with bench card.
   */
  private def tryChargeRetreatAndSwap(p : Player, bIndex : Int) {
      val active = p.active.get
      if (active.getTotalEnergy() >= retreatCost(p)) {
        p.discardEnergyFromCard(active, cnt = retreatCost(p))
        p.swapActiveAndBench(bIndex)
      }
  }

  private def chargeSpecificRetreatAndSwap(p : Player, bIndex : Int, args : Seq[String]) {
    val active = p.active.get
    val eCards : Seq[EnergyCard] = args(0).split(",").map(i => active.energyCards(i.toInt))
    p.discardSpecificEnergyFromCard(active, eCards)
    p.swapActiveAndBench(bIndex)
  }

  def handleDrag = (pData, dragCmd, args) => dragCmd match {

    case BenchToBench(bIndex1, bIndex2) => pData.owner.swapBenchCards(bIndex1, bIndex2)

    case BenchToActive(bIndex) => pData.owner.active match {
      case Some(active) => args.length match {
        case 0 => tryChargeRetreatAndSwap(pData.owner, bIndex)
        case _ => chargeSpecificRetreatAndSwap(pData.owner, bIndex, args)
      }
      case None => pData.owner.swapActiveAndBench(bIndex)
    }

    case ActiveToBench(bIndex) => args.length match {
      case 0 => tryChargeRetreatAndSwap(pData.owner, bIndex)
      case _ => chargeSpecificRetreatAndSwap(pData.owner, bIndex, args)
    }

    case HandToActive(hIndex) => (pData.owner.hand(hIndex), pData.owner.active) match {
      // Moving basic pokemon from hand to active slot.
      case (bp : BasicPokemon, None) => pData.owner.moveHandToActive(hIndex)
      // Attaching energy card to active pokemon.
      case (ec : EnergyCard, Some(active)) => pData.owner.attachEnergyFromHand(active, hIndex)
      // Evolving active pokemon.
      case (ep : EvolvedPokemon, Some(active)) => if (ep.isEvolutionOf(active)) {
        pData.owner.evolveActiveCard(hIndex)
      }
      case _ => ()
    }

    case HandToBench(hIndex, bIndex) => (pData.owner.hand(hIndex), pData.owner.bench(bIndex)) match {
      // Moving basic pokemon to empty bench slot.
      case (bp : BasicPokemon, None) => pData.owner.moveHandToBench(hIndex, bIndex)
      // Attaching energy card to bench pokemon.
      case (ec : EnergyCard, Some(bc)) => pData.owner.attachEnergyFromHand(bc, hIndex)
      // Evolving this bench pokemon.
      case (ep : EvolvedPokemon, Some(bc)) => if (ep.isEvolutionOf(bc)) {
        pData.owner.evolveBenchCard(hIndex, bIndex)
      }
      case _ => ()
    }

  }

  override def requestAdditional = (pData, dragCmd, args) => dragCmd match {
    case BenchToActive(bIndex) => args.length match {
      case 0 => checkForAmbiguity(pData.owner)
      case _ => None
    }
    case ActiveToBench(bIndex) => args.length match {
      case 0 => checkForAmbiguity(pData.owner)
      case _ => None
    }
    case _ => None
  }

}
