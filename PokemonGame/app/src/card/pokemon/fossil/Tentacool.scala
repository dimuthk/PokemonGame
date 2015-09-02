package src.card.pokemon.fossil

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

class Tentacool extends BasicPokemon(
    "Tentacool",
    "Tentacool-Fossil-56.jpg",
    Deck.FOSSIL,
    Identifier.TENTACOOL,
    id = 72,
    maxHp = 30,
    firstMove = Some(new ActivePokemonPower(
      "Cowardice") {
        def perform = (owner, opp, args) => {
          val tenta = owner.ownerOfMove(this).get
          val cards = tenta.pickUp() diff List(tenta)
          if (owner.active == Some(tenta)) {
            owner.clearActive()
          } else {
            for (i <- 0 until 5) {
              if (owner.bench(i) == Some(tenta)) {
                owner.bench(i) = None
              }
            }
          }
          owner.garbage = owner.garbage ++ cards
          owner.addCardToHand(tenta)
          None
        }
      }),
    secondMove = Some(new Move(
      "Acid",
      1,
      Map(EnergyType.WATER -> 1)) {
        def perform = (owner, opp, args) => standardAttack(owner, opp, 10)
      }),
    energyType = EnergyType.WATER,
    weakness = Some(EnergyType.THUNDER),
    retreatCost = 0)