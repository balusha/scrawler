package scrawler.crawling

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpEntity, HttpRequest, Uri}
import org.jsoup.Jsoup

import scala.concurrent.Future

trait Getter {
  def getTitle(uri: Uri)(implicit as: ActorSystem): Future[String]
}

class GetterImpl extends Getter {
  def getTitle(uri: Uri)(implicit as: ActorSystem) = {
    for {
      resp <- Http().singleRequest(HttpRequest(uri = uri))
    } yield Jsoup.parse(resp.entity(as[String]))
      .head()
      .getElementsByTag("title")
      .first()
      .text()
  }
}
