package src.card.pokemon.jungle

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.board.move.DefaultMoveInterpreter
import src.board.move.CustomMoveInterpreter
import src.board.intermediary.IntermediaryRequest
import src.board.intermediary.ClickableCardRequest
import src.card.condition.PreventDamageCondition
import src.card.pokemon._
import src.card.Deck

import play.api.Logger

class Butterfree extends BasicPokemon(
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

private class MegaDrain extends Move(
    "Mega Drain",
    4,
    Map(EnergyType.GRASS -> 4)) {

  override def perform(owner : Player, opp : Player) {
    val dmg = calculateDmg(owner, opp, 40)
    standardAttack(owner, opp, 40)
    owner.active.get.heal(roundUp(dmg / 2))
  }

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
        "FLIP<>MOVE<>INTERMEDIARY<>",
        p,
        1,
        benchCards)

    override def additionalRequest(owner : Player, opp : Player) : Option[IntermediaryRequest] = {
        val benchCards = opp.bench.toList.flatten
        if (benchCards.length > 1) {
            moveInterpreter.get.isActive = true
            return Some(new NextActiveSpecification(opp, owner.active.get, benchCards))
        }
        return None
    }

    override def perform(owner : Player, opp : Player) = standardAttack(owner, opp, 20)

}

class WhirlwindInterpreter extends CustomMoveInterpreter {

    override def handleIntermediary(owner : Player, opp : Player, cmds : Seq[String]) : Unit = {
        Logger.debug("handle intermediary")
        // the response comes from a flattened bench, so you need to convert back
        val benchIndex : Int = cmds(0).toInt
        var matcher : Int = -1
        standardAttack(owner, opp, 20)
        for (realIndex : Int <- 0 until 5) {
            if (opp.bench(realIndex).isDefined) {
                matcher = matcher + 1
            }
            if (matcher == benchIndex) {
                Logger.debug(benchIndex + " ")
                var temp : Option[PokemonCard] = opp.active
                opp.active = opp.bench(realIndex)
                opp.bench(realIndex) = temp
                flipTurn(opp, owner)
                return
            }
        }
        isActive = false
    }

    def attack(owner : Player, opp : Player, move : Move) : Unit = ()
}
