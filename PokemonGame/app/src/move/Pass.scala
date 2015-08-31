package src.move

import src.card.pokemon.PokemonCard
import play.api.libs.json._
import src.board.move.MoveCommand
import src.board.intermediary.IntermediaryRequest
import src.board.drag.CustomDragInterpreter
import src.board.state.CustomStateGenerator
import src.card.energy.EnergyCard
import src.card.energy.EnergyType
import src.json.Identifier
import src.json.Jsonable
import src.player.Player

import play.api.Logger

final class Pass extends Move("Pass", 0) {

  def perform = (owner, opp, args) => None

}