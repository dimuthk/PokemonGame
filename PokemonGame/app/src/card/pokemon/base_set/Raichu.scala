package src.card.pokemon.base_set

import src.card.Deck
import src.card.pokemon._
import src.json.Identifier
import src.move._
import src.move.MoveBuilder._
import src.move.ActivePokemonPower
import src.board.drag.CustomDragInterpreter
import src.player.Player
import src.card.energy.EnergyCard
import src.card.energy.EnergyType
import src.card.energy.WaterEnergy

class Raichu extends StageOnePokemon(
    "Raichu",
    "Raichu-Base-Set-14.jpg",
    Deck.BASE_SET,
    Identifier.RAICHU,
    id = 26,
    maxHp = 80,
    firstMove = Some(new Agility(
      "Agility",
      3,
      20,
      Map(EnergyType.THUNDER -> 1))),
    secondMove = Some(new Move(
      "Thunder",
      4,
      Map(EnergyType.THUNDER -> 3)) {
        def perform = (owner, opp, args) => selfDamageChanceAttack(owner, opp, 60, 30)
      }),
    energyType = EnergyType.THUNDER,
    weakness = Some(EnergyType.FIGHTING),
    retreatCost = 1)