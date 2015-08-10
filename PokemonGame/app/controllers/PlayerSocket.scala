package controllers

import actors.MyWebSocketActor
import play.api._
import play.api.mvc._
import play.api.Play.current

class PlayerSocket extends Controller {

  var id : Int = 0

  def index = WebSocket.acceptWithActor[String, String] { request => out => {
      id += 1
      PlayerActor.props(out, id)
    }
  }

}
