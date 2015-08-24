package src.board.drag

import src.card.Card
import src.card.energy.EnergyCard
import src.card.pokemon.PokemonCard
import src.card.pokemon.EvolutionStage
import src.board.drag._
import src.player.Player

object DefaultDragInterpreter extends DragInterpreter {

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

    override def swapActiveAndBench(p : Player, benchIndex : Int) {
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
          case ec : EnergyCard => {
            active.energyCards = active.energyCards ++ List(ec)
            p.hand = p.hand.slice(0, handIndex) ++ p.hand.slice(handIndex + 1, p.hand.size)
          }
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
          case ec : EnergyCard => {
            benchCard.energyCards = benchCard.energyCards ++ List(ec)
            p.hand = p.hand.slice(0, handIndex) ++ p.hand.slice(handIndex + 1, p.hand.size)
          }
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
