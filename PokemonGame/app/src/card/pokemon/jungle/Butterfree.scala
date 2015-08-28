package src.card.pokemon.jungle

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.board.move.CustomMoveInterpreter
import src.board.intermediary.IntermediaryRequest
import src.board.intermediary.ClickableCardRequest
import src.card.condition.PreventDamageCondition
import src.card.pokemon._
import src.card.Deck

class Butterfree extends StageTwoPokemon(
    "Butterfree",
    "Butterfree-Jungle-33.jpg",
    Deck.JUNGLE,
    Identifier.BUTTERFREE,
    id = 12,
    maxHp = 70,
    firstMove = Some(new Whirlwind()),
    secondMove = None,
    energyType = EnergyType.GRASS,
    weakness = Some(EnergyType.FIRE),
    resistance = Some(EnergyType.FIGHTING),
    retreatCost = 0)

class WhirlwindInterpreter extends CustomMoveInterpreter {

    // order is reversed because the intermediary response came from opponent.
    override def handleIntermediary(opp : Player, owner : Player, cmds : Seq[String]) : Unit = {
        // the response comes from a flattened bench, so you need to convert back
        val benchIndex : Int = cmds(0).toInt
        var realIndex : Int = benchIndex
        for (i : Int <- 0 until 5) {
            if (opp.bench(i).isEmpty) {
                realIndex = realIndex + 1
                
            }
        }


    }

    def attack(owner : Player, opp : Player, move : Move) : Unit = ()
}

private class Whirlwind extends Move(
    "Whirlwind",
    2,
    Map(),
    moveInterpreter = Some(new WhirlwindInterpreter())) {

    class NextActiveSpecification(
        p : Player,
        user : PokemonCard,
        benchCards : Seq[PokemonCard]) extends ClickableCardRequest(
        "Choose Active Pokemon",
        user.displayName + " is whisking you away! Select a new active pokemon",
        "MOVE<>INTERMEDIARY<>",
        p,
        1,
        benchCards)

    override def additionalRequest(owner : Player, opp : Player) : Option[IntermediaryRequest] = {
        val benchCards = opp.bench.toList.flatten
        if (benchCards.length > 1) {
            return Some(new NextActiveSpecification(opp, owner.active.get, benchCards))
        }
        return None
    }

    // Move is handled via intermediary.
    override def perform(owner : Player, opp : Player) = ()

}