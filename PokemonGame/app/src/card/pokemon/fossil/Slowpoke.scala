package src.card.pokemon.fossil

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

class Slowpoke extends BasicPokemon(
    "Slowpoke",
    "Slowpoke-Fossil-55.jpg",
    Deck.FOSSIL,
    Identifier.SLOWPOKE,
    id = 79,
    maxHp = 50,
    firstMove = Some(new Move(
      "Spacing Out",
      1) {
        def perform = (owner, opp, args) => flippedHeads() match {
          case true => {
            owner.active.get.heal(10)
            None
          }
          case false => None
        }

        override def update = (owner, opp, pc, turnSwapped, isActive) => {
          super.update(owner, opp, pc, turnSwapped, isActive)
          if (pc.currHp == pc.maxHp) {
            status = Status.DISABLED
          }
        }
      }),
    // TODO: Add scavenge move after trainers implemented
    /*secondMove = Some(new Move(
      "Scavenge",
      2,
      Map(EnergyType.PSYCHIC -> 1)) {
        def perform = (owner, opp, args) => paralyzeAttackChance(owner, opp, 20)
      }),*/
    energyType = EnergyType.PSYCHIC,
    weakness = Some(EnergyType.PSYCHIC),
    retreatCost = 1)