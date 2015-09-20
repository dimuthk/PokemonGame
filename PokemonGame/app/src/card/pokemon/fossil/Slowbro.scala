package src.card.pokemon.fossil

import src.card.Deck
import src.card.pokemon._
import src.json.Identifier
import src.move.Move
import src.board.drag._
import src.board.state.CustomStateGenerator
import src.board.state.DefaultStateGenerator
import src.move.MoveBuilder._
import src.move.ActivePokemonPower
import src.player.Player
import src.card.CardUI
import src.card.CardUI._
import src.card.energy.EnergyCard
import src.card.energy.EnergyType
import src.card.energy.WaterEnergy

class Slowbro extends StageOnePokemon(
    "Slowbro",
    "Slowbro-Fossil-43.jpg",
    Deck.FOSSIL,
    Identifier.SLOWBRO,
    id = 80,
    maxHp = 60,
    firstMove = Some(new ActivePokemonPower(
      "Strange Behavior",
      dragInterpreter = Some(new StrangeBehaviorDrag()),
      stateGenerator = Some(new StrangeBehaviorState())) {
        def perform = (owner, opp, args) => togglePower()
    }),
    secondMove = Some(new Move(
      "Psyshock",
      2,
      Map(EnergyType.PSYCHIC -> 2)) {
        def perform = (owner, opp, args) => paralyzeAttackChance(owner, opp, 20)
      }),
    energyType = EnergyType.PSYCHIC,
    weakness = Some(EnergyType.PSYCHIC),
    retreatCost = 1)

private class StrangeBehaviorState extends CustomStateGenerator {

  override def uiForActive = (p, isSouth) => isSouth match {
    case true => Set(FACE_UP, DRAGGABLE)
    case false => DefaultStateGenerator.uiForActive(p, isSouth)  
  }

  override def uiForBench = (p, isSouth) => isSouth match {
    case true => Set(FACE_UP, DRAGGABLE)
    case false => DefaultStateGenerator.uiForBench(p, isSouth)  
  }
  
  def uiForActivatedCard = (p) => Set(FACE_UP, CLICKABLE, DISPLAYABLE, DRAGGABLE, USABLE)

}

private class StrangeBehaviorDrag extends CustomDragInterpreter {

  override def handleCommand = (pData, dragCmd, _) => {
    val p = pData.owner
    val slowbro = p.cardWithActivatedPower
    dragCmd match {
      case BenchToBench(bIndex1, bIndex2) => p.bench(bIndex2) match {
        case Some(slowbro) => swapDamage(p.bench(bIndex1).get, slowbro)
        case _ => ()
      }
      case BenchToActive(bIndex) => p.active match {
        case Some(slowbro) => swapDamage(p.bench(bIndex).get, slowbro)
        case _ => ()
      }
      case ActiveToBench(bIndex) => p.bench(bIndex) match {
        case Some(slowbro) => swapDamage(p.active.get, slowbro)
        case _ => ()
      }
      case _ => ()
    }
  }

  private def swapDamage(drag : PokemonCard, drop : PokemonCard) : Unit = {
    if (drop.currHp > 10 && drag.currHp < drag.maxHp) {
      drag.heal(10)
      drop.takeDamage(None, 10)
    }
  }

}