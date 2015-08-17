package controllers

import scala.concurrent.Future
import src.board.Board
import actors.PlayerActor
import play.api._
import play.api.mvc._
import play.api.Play.current

class PlayerSocket extends Controller {

  def index = WebSocket.tryAcceptWithActor[String, String] {
    request => Future.successful(Board.canAccept match {
      case false => Left(Forbidden)
      case true => Right(Board.forkPlayerActor)
    })
  }

}
