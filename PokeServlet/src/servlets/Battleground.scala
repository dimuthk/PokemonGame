package servlets

import javax.servlet._
import javax.servlet.http.{HttpServlet,
  HttpServletRequest => HSReq, HttpServletResponse => HSResp}

/**
 * Entry point for app. Generates an html battleground layout for the user.
 */
class Battleground extends HttpServlet {
  override def doGet(req : HSReq, resp : HSResp) =
    req.getRequestDispatcher("/battleground.html").forward(req, resp)
}