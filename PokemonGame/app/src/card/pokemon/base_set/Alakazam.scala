package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.move.ActivePokemonPower
import src.board.intermediary._
import src.board.drag._
import src.board.drag.CustomDragInterpreter
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

class Alakazam extends BasicPokemon(
    "Alakazam",
    "Alakazam-Base-Set-1.jpg",
    Deck.BASE_SET,
    Identifier.ALAKAZAM,
    id = 65,
    maxHp = 80,
    firstMove = Some(new ActivePokemonPower(
      "Damage Swap",
      dragInterpreter = Some(new DamageSwapDrag()),
      stateGenerator = Some(new DamageSwapState())) {
        def perform = (owner, opp, args) => togglePower()
    }
    ),
    secondMove = Some(new Move(
      "Confuse Ray",
      3,
      Map(EnergyType.PSYCHIC -> 3)) {
      def perform = (owner, opp, args) => confuseAttackChance(owner, opp, 30)
    }),
    energyType = EnergyType.PSYCHIC,
    weakness = Some(EnergyType.PSYCHIC),
    retreatCost = 3)


private class DamageSwapState extends CustomStateGenerator {

  def uiForActiveSouth = (p) => p.isTurn match {
    case true => Set(FACE_UP, DRAGGABLE)
    case false => Set(FACE_UP)
  }

  def uiForBenchSouth = (p) => p.isTurn match {
    case true => Set(FACE_UP, DRAGGABLE)
    case false => Set(FACE_UP) 
  }

  def uiForHandSouth = (p) => Set()

  def uiForPrizeSouth = (p) => Set()

}

private class DamageSwapDrag extends CustomDragInterpreter {

  override def handleCommand = (pData, dragCmd, _) => dragCmd match {
    case ActiveToBench(bIndex) => swapDamage(pData.owner.active.get, pData.owner.bench(bIndex))
    case BenchToActive(bIndex) => swapDamage(pData.owner.bench(bIndex).get, pData.owner.active)
    case BenchToBench(bIndex1, bIndex2) => swapDamage(pData.owner.bench(bIndex1).get, pData.owner.bench(bIndex2))
    case _ => ()
  }

  private def swapDamage(
    drag : PokemonCard, dropOption : Option[PokemonCard]) : Unit = dropOption match {
      case Some(drop) => if (drop.currHp > 10 && drag.currHp < drag.maxHp) {
        drag.heal(10)
        drop.takeDamage(None, 10)
      }
      case None => ()
  }

}

