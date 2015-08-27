package src.board.drag

import src.board.intermediary.ClickableCardRequest
import src.board.intermediary.IntermediaryRequest
import src.card.Card
import src.card.energy.EnergyCard
import src.card.pokemon.PokemonCard
import src.card.pokemon.EvolutionStage
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

  override def additionalRequest(p : Player, cmd : DragCommand) : Option[IntermediaryRequest] = {
    cmd match {
      case BenchToActive(benchIndex : Int) => return checkActiveRestoreAmbiguity(p, p.active)
      case ActiveToBench(benchIndex : Int) => return checkActiveRestoreAmbiguity(p, p.active)
      case _ => return None
    }
  }

  private def checkActiveRestoreAmbiguity(p : Player, active : Option[PokemonCard]): Option[RetreatEnergySpecification] = {
    if (active.isDefined && active.get.retreatCost > 0 && active.get.energyCards.length > 1) {
      if (hasMultipleEnergyTypes(active.get.energyCards)) {
        return Some(new RetreatEnergySpecification(p, active.get.retreatCost, active.get.energyCards))
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

  // tmpl: benchIndex<>[indices of selected energyCards]
  override def handleIntermediary(cmd : Seq[String]) {
    val benchIndex : Int = cmd(0).toInt - 1
    val eCardIndices : Seq[Int] = cmd(1).split(",").map(_.toInt)
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
    override def benchToActive(p : Player, benchIndex : Int) {
      if (p.active.isDefined) {
        swapActiveAndBench(p, benchIndex)
      } else {
        p.active = Some(p.bench(benchIndex).get)
        p.bench(benchIndex) = None
      }
    }

    override def activeToBench(p : Player, benchIndex : Int) {
      if (p.bench(benchIndex).isDefined) {
        swapActiveAndBench(p, benchIndex)
      } else {
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

    override def handToActive(p : Player, handIndex : Int) {
      val card : Card = p.hand(handIndex)
      if (p.active.isEmpty) {
        card match {
          // Moving basic pokemon from hand to active slot.
          case pc : PokemonCard => {
            if (pc.evolutionStage == EvolutionStage.BASIC) {
              p.active = Some(pc)
              p.hand = p.hand.slice(0, handIndex) ++ p.hand.slice(handIndex + 1, p.hand.size)
            }
          }
          case _ => ()
        }
      } else {
        val active = p.active.get
        card match {
          // Attaching energy card to active pokemon.
          case ec : EnergyCard => attachEnergyToPokemon(p, ec, handIndex, active)
          // Evolving active pokemon.
          case pc : PokemonCard => {
            if (pc.isEvolutionOf(active)) {
              pc.evolveOver(active)
              p.active = Some(pc)
              p.hand = p.hand.slice(0, handIndex) ++ p.hand.slice(handIndex + 1, p.hand.size)
            }
          }
          case _ => ()
        }
      }
    }

    override def handToBench(p : Player, handIndex : Int, benchIndex : Int) {
      val card : Card = p.hand(handIndex)
      if (p.bench(benchIndex).isEmpty) {
        card match {
          case pc : PokemonCard => {
            // Moving basic pokemon to empty bench slot.
            if (pc.evolutionStage == EvolutionStage.BASIC) {
              p.bench(benchIndex) = Some(pc)
              p.hand = p.hand.slice(0, handIndex) ++ p.hand.slice(handIndex + 1, p.hand.size)
            }
          }
          case _ => ()
        }
      } else {
        val benchCard = p.bench(benchIndex).get
        card match {
          case ec : EnergyCard => attachEnergyToPokemon(p, ec, handIndex, benchCard)
          // Evolving this bench pokemon.
          case pc : PokemonCard => {
            if (pc.isEvolutionOf(benchCard)) {
              pc.evolveOver(benchCard)
              p.bench(benchIndex) = Some(pc)
              p.hand = p.hand.slice(0, handIndex) ++ p.hand.slice(handIndex + 1, p.hand.size)
            }
          }
          case _ => ()
        }
      }
    }


}
