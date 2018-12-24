package scrawler.crawling

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpEntity, HttpRequest, Uri}
import akka.http.scaladsl.unmarshalling.{PredefinedFromEntityUnmarshallers, Unmarshal}
import akka.stream.Materializer
import org.jsoup.Jsoup

import scala.concurrent.{ExecutionContext, Future}

trait Getter {
  def getTitle(uri: Uri)
              (implicit as: ActorSystem, ec: ExecutionContext, mat: Materializer): Future[String]
}

class GetterImpl extends Getter {
  def getTitle(uri: Uri)
              (implicit as: ActorSystem, ec: ExecutionContext, mat: Materializer) = {
    Http()
      .singleRequest(HttpRequest(uri = uri))
      .flatMap{ resp =>
        val i = Unmarshal(resp.entity).to[String]
        i.map {
          html =>
            Jsoup.parse(html)
              .head()
              .getElementsByTag("title")
              .first()
              .text()
        }
      }
  }
}
