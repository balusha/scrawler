package scrawler.crawling

import java.util.concurrent.Executors

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.Http
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{ActorMaterializer, Materializer}
import org.jsoup.Jsoup

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}
import scrawler.model.ParsingError

trait Getter {
  def getTitle(uri: Uri): Future[Either[ParsingError.type, String]]
}

class GetterImpl() extends Getter {

  implicit val getterAS: ActorSystem = ActorSystem("GetterSystem")
  implicit val getterMaterializer: ActorMaterializer = ActorMaterializer() // not sure it`s neccessary
  implicit val getterEC: ExecutionContext = getterAS.dispatcher

  def getTitle(uri: Uri): Future[Either[ParsingError.type, String]]  = {
    getResponseFromUri(uri).flatMap {
      case Left(err) => Future.successful(Left(err))
      case Right(response) => extractTitleFromResponse(response)
    }
  }

  private def extractTitleFromResponse(resp: HttpResponse)= {
    val ent = resp.entity
    val i = Unmarshal(ent).to[String]
    i.map {
      html =>
        Option(Jsoup.parse(html).getElementsByTag("head").first())
          .flatMap(head => Option(head.getElementsByTag("title").first()))
          .map(title => Right(title.text()))
          .getOrElse(Left(ParsingError))
    }
  }

  private def getResponseFromUri(uri: Uri): Future[Either[ParsingError.type,HttpResponse]] = {
    Http().singleRequest(HttpRequest(uri = uri)).flatMap {
      case resp@HttpResponse(StatusCodes.Redirection(_), headers, _, _) =>
        extractUriToRedirect(resp) match {
          case Left(err) => Future.successful(Left(err))
          case Right(url) => getResponseFromUri(url)
        }
      case resp => Future.successful(Right(resp))
    }
  }

  private def extractUriToRedirect(resp: HttpResponse) =
    resp.headers.find(_.name()=="Location").map{
    header =>
      Try {
        val headerContent = header.value()
        Uri(headerContent)
      } match {
        case Success(uri) => Right(uri)
        case Failure(exception) => Left(ParsingError)
      }
    }.getOrElse(Left(ParsingError))

}
