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
          headers.find(_.name()=="Location").map{
            header =>
              Http()
                .singleRequest(HttpRequest(uri = header.value()))
                .flatMap {
                  case resp =>
                    val ent = resp.entity
                    val i = Unmarshal(ent).to[String]
                    i.map {
                      html =>
                        Option(Jsoup.parse(html).getElementsByTag("head").first())
                          .flatMap(head => Option(head.getElementsByTag("title").first()))
                          .map(title => title.text())
                          .getOrElse("There is no head\\title tag")
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
