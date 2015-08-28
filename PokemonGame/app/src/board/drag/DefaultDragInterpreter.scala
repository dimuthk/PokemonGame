package src.board.drag

import play.api.Logger
import src.board.intermediary.ClickableCardRequest
import src.board.intermediary.IntermediaryRequest
import src.card.Card
import src.card.energy.EnergyCard
import src.card.pokemon._
import src.board.drag._
import src.player.Player

class RetreatEnergySpecification(
  p : Player, clickCount : Int, energyCards : Seq[EnergyCard], benchIndex : Int) extends ClickableCardRequest(
  "Discard Energy",
  "Select the energy cards you want to discard.",
  "DRAG<>INTERMEDIARY<>" + benchIndex + "<>",
  p,
  clickCount,
  energyCards)

object DefaultDragInterpreter extends DragInterpreter {

  val restrictEnergyAttach = false

  override def additionalRequest(p : Player, cmd : DragCommand) : Option[IntermediaryRequest] = cmd match {
    case BenchToActive(benchIndex : Int) => checkActiveRestoreAmbiguity(p, p.active, benchIndex)
    case ActiveToBench(benchIndex : Int) => checkActiveRestoreAmbiguity(p, p.active, benchIndex)
    case _ => None
  }

  private def checkActiveRestoreAmbiguity(p : Player, active : Option[PokemonCard], benchIndex : Int): Option[RetreatEnergySpecification] = {
    if (active.isDefined && active.get.retreatCost > 0 && active.get.energyCards.length > 1) {
      if (hasMultipleEnergyTypes(active.get.energyCards)) {
        return Some(new RetreatEnergySpecification(p, active.get.retreatCost, active.get.energyCards, benchIndex))
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
  override def handleIntermediary(p : Player, cmd : Seq[String]) {
    val benchIndex : Int = cmd(0).toInt
    val eCardIndices : Seq[Int] = cmd(1).split(",").map(_.toInt)
    val active = p.active.get
    if (p.bench(benchIndex).isDefined) {
      val benchCard = p.bench(benchIndex).get
      p.active = Some(benchCard)
    } else {
      p.active = None
    }
    p.bench(benchIndex) = Some(active)
    p.bench(benchIndex).get.energyCards = p.bench(benchIndex).get.energyCards
      .zipWithIndex
      .filterNot { case (_, index) => eCardIndices.contains(index) }
      .map(_._1)
  }

    override def benchToBench(p : Player, benchIndex1 : Int, benchIndex2 : Int) {
      val benchOne = p.bench(benchIndex1).get
      if (p.bench(benchIndex2).isDefined) {
        p.bench(benchIndex1) = Some(p.bench(benchIndex2).get)
        p.bench(benchIndex2) = Some(benchOne)
      } else {
        p.bench(benchIndex2) = Some(benchOne)
        p.bench(benchIndex1) = None
      }
    }

    /**
     * Assumes benchIndex points to a non-null bench card.
     */
    override def benchToActive(p : Player, benchIndex : Int) : Unit = p.active match {
      case Some(_) => swapActiveAndBench(p, benchIndex)
      case None => {
        p.active = Some(p.bench(benchIndex).get)
        p.bench(benchIndex) = None
      }
    }

    override def activeToBench(p : Player, benchIndex : Int) : Unit = p.bench(benchIndex) match {
      case Some(_) => swapActiveAndBench(p, benchIndex)
      case None => {
        val active = p.active.get
        if (active.getTotalEnergy() >= active.retreatCost) {
          active.energyCards = active.energyCards.dropRight(active.retreatCost)
          p.bench(benchIndex) = Some(active)
          p.active = None
        }
      }
    }

    private def swapActiveAndBench(p : Player, benchIndex : Int) {
      val active = p.active.get
      val benchCard = p.bench(benchIndex).get
      if (active.getTotalEnergy() >= active.retreatCost) {
        active.energyCards = active.energyCards.dropRight(active.retreatCost)
        p.active = Some(benchCard)
        p.bench(benchIndex) = Some(active)
      }
    }

    override def handToActive(p : Player, handIndex : Int) : Unit = (p.hand(handIndex), p.active) match {
      // Moving basic pokemon from hand to active slot.
      case (bp : BasicPokemon, None) => {
        p.active = Some(bp)
        p.removeCardFromHand(handIndex)
      }
      // Attaching energy card to active pokemon.
      case (ec : EnergyCard, Some(active)) => attachEnergyToPokemon(p, ec, handIndex, active)
      // Evolving active pokemon.
      case (ep : EvolvedPokemon, Some(active)) => {
        if (ep.isEvolutionOf(active)) {
          ep.evolveOver(active)
          p.active = Some(ep)
          p.removeCardFromHand(handIndex)
        }
      }
      case _ => ()
    }

    override def handToBench(p : Player, handIndex : Int, benchIndex : Int) : Unit = (p.hand(handIndex), p.bench(benchIndex)) match {
      // Moving basic pokemon to empty bench slot.
      case (bp : BasicPokemon, None) => {
        p.bench(benchIndex) = Some(bp)
        p.removeCardFromHand(handIndex)
      }
      // Attaching energy card to bench pokemon.
      case (ec : EnergyCard, Some(bc)) => attachEnergyToPokemon(p, ec, handIndex, bc)
      // Evolving this bench pokemon.
      case (ep : EvolvedPokemon, Some(bc)) => {
        if (ep.isEvolutionOf(bc)) {
          ep.evolveOver(bc)
          p.bench(benchIndex) = Some(ep)
          p.removeCardFromHand(handIndex)
        }
      }
      case _ => ()
    }

}
