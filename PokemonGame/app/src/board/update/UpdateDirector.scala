package src.board.update

import src.board.Director
import src.board.intermediary.IntermediaryRequest
import play.api.Logger
import src.card.pokemon.PokemonCard
import src.player.Player
import src.move._

/**
 * Polishes up the board before sending out to the client. This includes updating
 * statuses of moves, and detecting if the active on either side is knocked out,
 * in which case a request is generated for the corresponding player. 
 */
object UpdateDirector extends Director[UpdateCommand, UpdateInterpreter]("UPDATE") {

  def buildCommand = (args) => args.length match {
    case 0 => Update()
    case _ => throw new Exception("unsupported right now")
  }

  def selectInterpreterArgs = (args) => args

  def selectInterpreter(owner : Player, opp : Player) = DefaultUpdateInterpreter

}