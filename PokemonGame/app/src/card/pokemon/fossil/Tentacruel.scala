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

class Tentacruel extends StageOnePokemon(
    "Tentacruel",
    "Tentacruel-Fossil-44.jpg",
    Deck.FOSSIL,
    Identifier.TENTACRUEL,
    id = 73,
    maxHp = 60,
    firstMove = Some(new Move(
      "Supersonic",
      1,
      Map(EnergyType.WATER -> 1)) {
        def perform = (owner, opp, args) => confuseAttackChance(owner, opp)
      }),
    secondMove = Some(new Move(
      "Jellyfish Sting",
      2,
      Map(EnergyType.WATER -> 2)) {
        def perform = (owner, opp, args) => poisonAttack(owner, opp, 10)
      }),
    energyType = EnergyType.WATER,
    weakness = Some(EnergyType.THUNDER),
    retreatCost = 0)