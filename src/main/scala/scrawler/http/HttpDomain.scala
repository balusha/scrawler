package scrawler.http

import akka.http.scaladsl.model.Uri
import scrawler.model.{CrawlingError, CrawlingId}

import scrawler.http.CrawlingResultResponse.CrawlingResult

case class NewCrawlingRequest(urls: Seq[Uri])

case class NewCrawlingResponse(id: CrawlingId)

class CrawlingResultResponse(val id: CrawlingId, crawlingResults: CrawlingResult) {
  val results = crawlingResults.map { case (url, res) =>
      url -> (res match {
        case None => "Parsing in progress"
        case Some(Left(err)) => "Crawling error" // TODO interpolate err description
        case Some(Right(value)) => value
      })
    }

  override def toString() = s"CrawlingResultResponse(id:$id results:$results)"

}

object CrawlingResultResponse{
  type CrawlingResult = Seq[(Uri, CrawlingResultPart)]
  type CrawlingResultPart = Option[Either[CrawlingError, String]]
}

trait EndpointError {
  def message: String
}

object EndpointError {
  case class InternalServerError(message: String) extends EndpointError
  case class ParsingError(message: String) extends EndpointError
}