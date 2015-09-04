package src.card.pokemon.jungle

import src.json.Identifier
import src.move._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Snorlax extends BasicPokemon(
	"Snorlax",
	"Snorlax-Jungle-11.jpg",
	Deck.JUNGLE,
	Identifier.SNORLAX,
	id = 143,
	maxHp = 90,
	firstMove = Some(new PassivePokemonPower("Thick Skinned") {}),
	secondMove = Some(new Move(
      "Body Slam",
      4) {
        def perform = (owner, opp, args) => paralyzeAttackChance(owner, opp, 30)
      }),
	energyType = EnergyType.COLORLESS,
	weakness = Some(EnergyType.FIGHTING),
	resistance = Some(EnergyType.PSYCHIC),
	retreatCost = 4)