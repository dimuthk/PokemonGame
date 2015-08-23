package src.card.pokemon

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.move.PokemonPower
import src.player.Player
import src.card.energy.EnergyCard
import src.card.energy.EnergyType

class Charizard extends PokemonCard(
    "Charizard",
    "Charizard-Base-Set-4.jpg",
    Identifier.CHARIZARD,
    id = 6,
    maxHp = 120,
    firstMove = Some(new EnergyBurn()),
    secondMove = Some(new FireSpin()),
    energyType = EnergyType.FIRE,
    weakness = Some(EnergyType.WATER),
    resistance = Some(EnergyType.FIGHTING),
    retreatCost = 3,
    evolutionStage = EvolutionStage.STAGE_TWO) {

  override def isEvolutionOf(pokemon : PokemonCard) = pokemon.id == 5

  override def discardEnergy(eType : EnergyType.Value, cnt : Int = 1) : Seq[EnergyCard] = {
    if (statusCondition == None) {
      return super.discardEnergy(EnergyType.COLORLESS, cnt)
    } else {
      return super.discardEnergy(eType, cnt)
    }
  }
}

private class EnergyBurn extends PokemonPower(
  "Energy Burn",
  isActivatable = false)

private class FireSpin extends Move(
  "Fire Spin",
  4,
  Map(EnergyType.FIRE -> 4)) {

  override def hasEnoughEnergy(p : Player, energyCards : Seq[EnergyCard]) : Boolean = {
    p.active.get match {
      case c : Charizard => {
        if (c.statusCondition == None) {
          val total = energyCards.map { card => card.energyCount }.sum
          return total >= totalEnergyReq
        } else {
          return super.hasEnoughEnergy(p, energyCards)
        }
      }
      case _ => throw new Exception("Charizard tried to make hasEnoughEnergy call when not active")
    }
  }

  override def perform(owner : Player, opp : Player) =
      energyDiscardAttack(owner, opp, 100, EnergyType.FIRE, 2)
}
