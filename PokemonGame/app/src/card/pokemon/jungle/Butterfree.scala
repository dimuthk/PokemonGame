package src.card.pokemon.jungle

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
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

private class Whirlwind extends Move(
    "Whirlwind",
    2,
    Map()) {

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