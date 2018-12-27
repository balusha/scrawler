package scrawler.http

import akka.http.scaladsl.model.Uri
import akka.stream.FlowMonitorState
import scrawler.model.{CrawlingError, CrawlingId, NoSuchCrawlingRequest}
import scala.language.implicitConversions

//import scrawler.http.CrawlingResultResponse.CrawlingResult

case class NewCrawlingRequest(urls: Seq[Uri])

case class NewCrawlingResponse(id: CrawlingId)

//class CrawlingResultResponse(val id: CrawlingId, crawlingResults: CrawlingResult) {
//  val results = crawlingResults.map { case (url, res) =>
//      url -> (res match {
//        case None => "Parsing in progress"
//        case Some(Left(err)) => "Crawling error" // TODO interpolate err description
//        case Some(Right(value)) => value
//      })
//    }
//
//  override def toString() = s"CrawlingResultResponse(id:$id results:$results)"
//
//}
//
//object CrawlingResultResponse{
//  type CrawlingResult = Seq[(Uri, CrawlingResultPart)]
//  type CrawlingResultPart = Option[Either[CrawlingError, String]]
//}
//
//case class CrawlingAttempt

trait EndpointError {
  def message: String
}

object EndpointError {
  case class InternalServerError(message: String) extends EndpointError
//  case class ParsingError(message: String) extends EndpointError
}

case class CrawlingResultResponse(id: CrawlingId, attempts: Seq[CrawlingAttempt])
case class CrawlingAttempt(url: Uri, result: CrawlingAttemptResult)

object CrawlingAttempt {

  implicit def seqres2attempt(seqres: Seq[(Uri, Option[Either[CrawlingError, String]])]): Seq[CrawlingAttempt] = {
    seqres.map { value => res2attempt(value)}
  }

  implicit def res2attempt(res: (Uri, Option[Either[CrawlingError, String]])): CrawlingAttempt = {
    val uri = res._1
    val attemptResult = res._2 match {
      case None => InProgress(status = AttemptStatus.Pending)
      case Some(rres) => rres match {
        case Left(err) => Failed(status = AttemptStatus.Error, description = err)
        case Right(title) => Succeed(status = AttemptStatus.Ok, title = title)
      }
    }
    CrawlingAttempt(uri, attemptResult)
  }
}

sealed trait AttemptStatus
object AttemptStatus {

  case object Pending extends AttemptStatus

  case object Ok extends AttemptStatus

  case object Error extends AttemptStatus

}

sealed trait CrawlingAttemptResult {
  val status: AttemptStatus
}

case class InProgress (
                     status: AttemptStatus
                   ) extends CrawlingAttemptResult

case class Succeed (
                  status: AttemptStatus,
                  title: String
                  ) extends CrawlingAttemptResult
case class Failed(
                   status: AttemptStatus,
                   description: CrawlingError
                 ) extends CrawlingAttemptResult