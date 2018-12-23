package scrawler

import akka.http.scaladsl.model.Uri
import scrawler.model.CrawlingError

import scala.concurrent.Future

trait ReqSender {
  def getTitle(targetURI: Uri): Future[Either[CrawlingError, String]]
}

