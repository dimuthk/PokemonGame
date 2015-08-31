package src.card.pokemon.base_set

import src.card.Deck
import src.card.pokemon._
import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.move.ActivePokemonPower
import src.board.drag.CustomDragInterpreter
import src.player.Player
import src.card.energy.EnergyCard
import src.card.energy.EnergyType
import src.card.energy.WaterEnergy

class Pikachu extends BasicPokemon(
    "Pikachu",
    "Pikachu-Base-Set-58.jpg",
    Deck.BASE_SET,
    Identifier.PIKACHU,
    id = 25,
    maxHp = 40,
    firstMove = Some(new Move(
      "Gnaw",
      1) {
        def perform = (owner, opp, args) => standardAttack(owner, opp, 10)
      }),
    secondMove = Some(new Move(
      "Thunder Jolt",
      2,
      Map(EnergyType.THUNDER -> 1)) {
        def perform = (owner, opp, args) => selfDamageChanceAttack(owner, opp, 30, 10)
      }),
    energyType = EnergyType.THUNDER,
    weakness = Some(EnergyType.FIGHTING),
    retreatCost = 1)