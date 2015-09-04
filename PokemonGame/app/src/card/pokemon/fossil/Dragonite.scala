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

class Dragonite extends StageTwoPokemon(
    "Dragonite",
    "Dragonite-Fossil-4.jpg",
    Deck.FOSSIL,
    Identifier.DRAGONITE,
    id = 149,
    maxHp = 100,
    firstMove = Some(new ActivePokemonPower("Step In") {
      def perform = (owner, opp, args) => togglePower()
    }),
    secondMove = Some(new Move(
    "Slam",
    4) {
      def perform = (owner, opp, args) => multipleHitAttack(owner, opp, 40, 2)
    }),
    energyType = EnergyType.COLORLESS,
    resistance = Some(EnergyType.FIGHTING),
    retreatCost = 1)