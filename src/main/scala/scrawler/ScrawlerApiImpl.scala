package scrawler

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import scrawler.http.NewCrawlingRequest
import scrawler.model.ResultsStorage
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport._
import scrawler.http.JsonCodecs.Encoders._
import scrawler.http.JsonCodecs.Decoders._
//import scrawler.http.JsonCodecs.Marshallers._

class ScrawlerApiImpl(
                     storage: ResultsStorage
                     ) extends ScrawlerApi {

  def route: Route = path("ping") {
    get {
      complete("pong")
    }
  } ~
  path("crawlingrequests") {
    post {
      entity(as[NewCrawlingRequest]) { ent =>
        complete(storage.register(ent.urls))
      }

    }
  }

}
