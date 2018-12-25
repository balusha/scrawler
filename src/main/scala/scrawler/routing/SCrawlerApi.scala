package scrawler.routing

import akka.http.scaladsl.server.Route

trait ScrawlerApi {

  def route: Route

}

