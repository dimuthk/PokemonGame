package src.card.pokemon.jungle

import src.json.Identifier
import src.move._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class Lickitung extends BasicPokemon(
	"Lickitung",
	"Lickitung-Jungle-38.jpg",
	Deck.JUNGLE,
	Identifier.LICKITUNG,
	id = 108,
	maxHp = 90,
	firstMove = Some(new Move(
      "Tongue Wrap",
      1) {
        def perform = (owner, opp, args) => paralyzeAttackChance(owner, opp, 10)
      }),
	secondMove = Some(new Move(
		"Supersonic",
        2) {
			def perform = (owner, opp, args) => confuseAttackChance(owner, opp)
        }),
	energyType = EnergyType.COLORLESS,
	weakness = Some(EnergyType.FIGHTING),
	resistance = Some(EnergyType.PSYCHIC),
	retreatCost = 3)