package controllers

import scala.concurrent.Future
import src.board.Board
import actors.PlayerActor
import play.api._
import play.api.mvc._
import play.api.Play.current

class PlayerSocket extends Controller {

  val board : Board = new Board()

  def index = WebSocket.tryAcceptWithActor[String, String] {
    request => Future.successful(board.canAccept() match {
      case false => Left(Forbidden)
      case true => Right(board.forkPlayerActor)
    })
  }

}
