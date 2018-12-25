package scrawler.crawling

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.Http
import akka.http.scaladsl.unmarshalling.Unmarshal
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
      .flatMap { resp =>
        resp match {
        case resp@HttpResponse(StatusCodes.Redirection(_), headers, _, _) => {
          headers.find {
            header =>
              header.name() == "RedirectTo"
          }.map{
            header =>
              Http()
                .singleRequest(HttpRequest(uri = Uri(header.value())))
                .flatMap {
                  case resp =>
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
          }.getOrElse(Future.successful("Smthng wnt wrng :(")) }
        case resp =>
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
  }}
}
