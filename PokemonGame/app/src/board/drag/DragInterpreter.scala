package src.board.drag

import src.card.Card
import src.card.energy.EnergyCard
import src.card.pokemon.PokemonCard
import src.card.pokemon.EvolutionStage
import src.board.drag._
import src.player.Player

abstract class DragInterpreter {

    final def handleDrag(p : Player, move : DragCommand) {
      move match {
        case HandToActive(handIndex : Int) => handToActive(p, handIndex)
        case HandToBench(handIndex : Int, benchIndex : Int) => handToBench(p, handIndex, benchIndex)
        case ActiveToBench(benchIndex : Int) => activeToBench(p, benchIndex : Int)
        case BenchToActive(benchIndex : Int) => benchToActive(p, benchIndex : Int)
        case BenchToBench(benchIndex1 : Int, benchIndex2 : Int) => benchToBench(p, benchIndex1, benchIndex2)
      }
    }

    def benchToBench(p : Player, benchIndex1 : Int, benchIndex2 : Int) : Unit

    def benchToActive(p : Player, benchIndex : Int) : Unit

    def activeToBench(p : Player, benchIndex : Int) : Unit

    def swapActiveAndBench(p : Player, benchIndex : Int) : Unit

    def handToActive(p : Player, handIndex : Int) : Unit

    def handToBench(p : Player, handIndex : Int, benchIndex : Int) : Unit

}
