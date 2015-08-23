package src.card.pokemon.base_set

import src.card.Deck
import src.card.pokemon._
import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.move.PokemonPower
import src.player.Player
import src.card.energy.EnergyCard
import src.card.energy.EnergyType
import src.card.energy.WaterEnergy

class Blastoise extends PokemonCard(
    "Blastoise",
    "Blastoise-Base-Set-2.jpg",
    Deck.BASE_SET,
    Identifier.BLASTOISE,
    id = 9,
    maxHp = 100,
    firstMove = Some(new RainDance()),
    secondMove = Some(new HydroPump()),
    energyType = EnergyType.WATER,
    weakness = Some(EnergyType.THUNDER),
    retreatCost = 3,
    evolutionStage = EvolutionStage.STAGE_TWO) {

  override def isEvolutionOf(pokemon : PokemonCard) = pokemon.id == 8

}

private class RainDance extends PokemonPower(
  "Rain Dance",
  isActivatable = true) {

  override def handleMove(owner : Player, opp : Player, moveName : String, itemMap : Map[String, Int]) {
    moveName match {
      case "HAND_TO_ACTIVE" => {
        if (owner.active.isEmpty) {
          return
        }
        val active = owner.active.get
        val handIndex = itemMap.getOrElse("drag", -1)
        owner.hand(handIndex) match {
          case ec : WaterEnergy => {
            active.energyCards = active.energyCards ++ List(ec)
            owner.removeCardFromHand(handIndex)
          }
          case _ => return
        }
      }
      case "HAND_TO_BENCH" => {
        val handIndex = itemMap.getOrElse("drag", -1)
        val benchIndex = itemMap.getOrElse("drop", -1)
        if (owner.bench(benchIndex).isEmpty) {
          return
        }
        val benchCard = owner.bench(benchIndex).get
        owner.hand(handIndex) match {
          case ec : WaterEnergy => {
            benchCard.energyCards = benchCard.energyCards ++ List(ec)
            owner.removeCardFromHand(handIndex)
          }
          case _ => return
        }

      }
      case _ => ()
    }
    
  }

}

private class HydroPump extends Move(
  "HydroPump",
  3,
  Map(EnergyType.WATER -> 3)) {

  override def perform(owner : Player, opp : Player)
      = standardAttackPlusExtra(owner, opp, 40, EnergyType.WATER, 3)
}
