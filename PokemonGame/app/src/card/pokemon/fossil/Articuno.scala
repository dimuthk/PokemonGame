package src.card.pokemon.fossil

import src.card.Deck
import src.move.Whirlwind
import src.move.Whirlwind._
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

class Articuno extends StageOnePokemon(
    "Articuno",
    "Articuno-Fossil-2.jpg",
    Deck.FOSSIL,
    Identifier.ARTICUNO,
    id = 144,
    maxHp = 70,
    firstMove = Some(new Move(
        "Freeze Dry",
        3,
        Map(EnergyType.WATER -> 3)) {
            def perform = (owner, opp, args) => paralyzeAttackChance(owner, opp, 30)
        }),
    secondMove = Some(new Move(
      "Blizzard",
      4,
      Map(EnergyType.GRASS -> 4)) {
        def perform = (owner, opp, args) => {
            val p = if (flippedHeads()) opp else owner
            for (bc : PokemonCard <- p.bench.toList.flatten) {
                bc.takeDamage(owner.active, 10)
            }
            standardAttack(owner, opp, 50)
        }
      }),
    energyType = EnergyType.WATER,
    resistance = Some(EnergyType.FIGHTING),
    retreatCost = 2)