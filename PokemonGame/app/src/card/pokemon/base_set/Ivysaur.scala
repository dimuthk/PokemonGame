package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Ivysaur extends StageOnePokemon(
    "Ivysaur",
    "Ivysaur-Base-Set-30.jpg",
    Deck.BASE_SET,
    Identifier.IVYSAUR,
    id = 2,
    maxHp = 60,
    firstMove = Some(new Move(
      "Vine Whip",
      3,
      Map(EnergyType.GRASS -> 1)) {
        def perform = (owner, opp ) => standardAttack(owner, opp, 30)
    }),
    secondMove = Some(new Move(
      "Poisonpowder",
      3,
      Map(EnergyType.GRASS -> 3)) {
        def perform = (owner, opp) => poisonAttack(owner, opp, 20)
    }),
    energyType = EnergyType.GRASS,
    weakness = Some(EnergyType.FIRE),
    retreatCost = 1)