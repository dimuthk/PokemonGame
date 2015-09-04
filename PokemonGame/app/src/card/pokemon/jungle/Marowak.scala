package src.card.pokemon.jungle

import src.json.Identifier
import src.move._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Marowak extends StageOnePokemon(
	"Marowak",
	"Marowak-Jungle-39.jpg",
	Deck.JUNGLE,
	Identifier.MAROWAK,
	id = 105,
	maxHp = 60,
	firstMove = Some(new Move(
      "Bonemerang",
      2,
      Map(EnergyType.FIGHTING -> 2)) {
        def perform = (owner, opp, args) => multipleHitAttack(owner, opp, 30, 2)
      }),
	secondMove = Some(new CallForFamily(
      "Call For Friend",
      selector = (card) => card match {
        case bp : BasicPokemon => bp.energyType == EnergyType.FIGHTING
        case _ => false
      },
      totalEnergyReq = 3,
      Map(EnergyType.FIGHTING -> 2))),
	energyType = EnergyType.FIGHTING,
	weakness = Some(EnergyType.GRASS),
	resistance = Some(EnergyType.THUNDER),
	retreatCost = 1)