package src.card.pokemon

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.move.PokemonPower
import src.player.Player
import src.card.energy.EnergyType

class Venusaur extends PokemonCard(
    "Venusaur",
    "Venusaur-Base-Set-15.jpg",
    id = 3,
    maxHp = 100,
    firstMove = Some(new EnergyTrans()),
    secondMove = Some(new Solarbeam()),
    energyType = EnergyType.GRASS,
    weakness = Some(EnergyType.FIRE),
    retreatCost = 2) {
    //evolutionStage = EvolutionStage.STAGE_TWO) {


  override def getIdentifier() = Identifier.VENUSAUR

  override def isEvolutionOf(pokemon : PokemonCard) = pokemon.id == 2

}

private class EnergyTrans extends PokemonPower(
  "Energy Trans",
  isActivatable = true)

private class Solarbeam extends Move(
  "Solarbeam",
  4,
  Map(EnergyType.GRASS -> 4)) {

  override def perform(owner : Player, opp : Player) = standardAttack(owner, opp, 60)
}
