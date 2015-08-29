package src.card.pokemon.base_set

import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Charmeleon extends StageOnePokemon(
    "Charmeleon",
    "Charmeleon-Base-Set-24.jpg",
    Deck.BASE_SET,
    Identifier.CHARMELEON,
    id = 2,
    maxHp = 80,
    firstMove = Some(new Move(
      "Slash",
      3) {
        def perform = (owner, opp) => standardAttack(owner, opp, 30)
      }),
    secondMove = Some(new Move(
      "Flamethrower",
      3,
      Map(EnergyType.FIRE -> 2)) {
        def perform = (owner, opp) => energyDiscardAttack(owner, opp, 50, EnergyType.FIRE)
      }),
    energyType = EnergyType.FIRE,
    weakness = Some(EnergyType.WATER),
    retreatCost = 1)