package controllers

import src.board.Board
import actors.PlayerActor
import play.api._
import play.api.mvc._
import play.api.Play.current

class PlayerSocket extends Controller {

  val board : Board = new Board()

  def index = WebSocket.acceptWithActor[String, String] {
    request => out => {
      board.forkPlayerActor(out)
    }
  }

}
