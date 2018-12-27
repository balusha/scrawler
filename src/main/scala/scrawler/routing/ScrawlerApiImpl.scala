package scrawler.routing

import akka.http.scaladsl.model.StatusCodes.{BadRequest, InternalServerError}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import scrawler.http.JsonCodecs.Encoders._
import scrawler.http.JsonCodecs.Decoders._
import scrawler.http.{CrawlingResultResponse, EndpointError, NewCrawlingRequest}
import scrawler.model.{CrawlingId, ParsingError, ResultsStorage}
import io.circe.generic.auto._
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport._

class ScrawlerApiImpl(
                     storage: ResultsStorage
                     ) extends ScrawlerApi {

  private val rh = RejectionHandler.newBuilder()
    .handle {
      case rejection: MalformedRequestContentRejection =>
        complete(BadRequest -> ParsingError)
      case rejection: ValidationRejection =>
        complete(BadRequest -> rejection)
    }.result()

  private val eh = ExceptionHandler {
    case _: Exception =>
      complete(InternalServerError -> EndpointError.InternalServerError("Smtng went wrong :("))
  }

  def route: Route =
    handleExceptions(eh) { handleRejections(rh) {
      path("ping") {
        get {
          complete("pong")
        }
      } ~
//        path("crawlingrequests") {
//
//        } ~
      pathPrefix("crawlingrequests"){
        pathEnd {
          post {
            entity(as[NewCrawlingRequest]) { ent =>
              complete(storage.register(ent.urls))
            }
          }
        } ~
        path(LongNumber) { id =>
          get {
              val crawlingId = CrawlingId(id)
              storage.getResult(crawlingId) match {
                case Some(crawlingResult) => complete {
                  new CrawlingResultResponse(crawlingId, crawlingResult)
                }
                //case None => reject(ValidationRejection)
                case None => complete { "WTF" }//reject //TODO there must be a way to explain rejection
              }
          }
        }
      }

      // TODO need a rejection and handling
      // TODO add synchronous variant of use
      // TODO add exception / rejection handling
    }}
}
