package scrawler

import akka.http.scaladsl.server.Route

trait ScrawlerApi {

  def route: Route

}

