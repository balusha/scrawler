package scrawler

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._

class ScrawlerApiImpl extends ScrawlerApi {

  def route: Route = path("ping") {
    get {
      complete("pong")
    }
  } ~
  path ("pung") {
    get {
      complete("parng")
    }
  }

}
