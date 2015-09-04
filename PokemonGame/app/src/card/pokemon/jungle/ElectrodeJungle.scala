package src.card.pokemon.base_set

import src.board.intermediary.IntermediaryRequest
import src.json.Identifier
import src.move.Move
import src.move.MoveBuilder._
import src.card.condition.StatusCondition
import src.player.Player
import src.card.energy.EnergyType
import src.card.pokemon._
import src.card.Deck

class ElectrodeJungle extends StageOnePokemon(
	"Electrode",
	"Electrode-Jungle-2.jpg",
	Deck.JUNGLE,
	Identifier.ELECTRODE_JUNGLE,
	id = 101,
	maxHp = 90,
	firstMove = Some(new Move(
		"Tackle",
        2) {
			def perform = (owner, opp, args) => standardAttack(owner, opp, 20)
        }),
	secondMove = Some(new Move(
		"Chain Lightning",
        3,
        Map(EnergyType.THUNDER -> 3)) {
			def perform = (owner, opp, args) => {
				val eType = owner.active.get.energyType
				for (pc : PokemonCard <- owner.bench.toList.flatten ++ opp.bench.toList.flatten) {
					if (pc.energyType == eType && eType != EnergyType.COLORLESS) {
						pc.takeDamage(owner.active, 10)
					}
				}
				standardAttack(owner, opp, 20)
			}
        }),
	energyType = EnergyType.THUNDER,
	weakness = Some(EnergyType.FIGHTING),
	retreatCost = 1)