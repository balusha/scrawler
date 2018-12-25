package scrawler.routing

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Route, ValidationRejection}
import scrawler.http.JsonCodecs.Encoders._
import scrawler.http.NewCrawlingRequest
import scrawler.model.{CrawlingId, ResultsStorage}
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport._

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
  } ~
  pathPrefix("crawlingrequests" / LongNumber) { id =>
    pathEnd {
      get {
        complete {
          val crawlingId      = CrawlingId(id)
          storage.getResult(crawlingId) match {
            case Some(crawlingResult) => Right(crawlingResult)
            case None => Left(ValidationRejection)
          }
        }
      }
    }
  }
  // TODO need a rejection and handling
  // TODO add synchronous variant of use
  // TODO add exception / rejection handling


}
