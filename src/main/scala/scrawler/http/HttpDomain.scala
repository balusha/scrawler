package scrawler.http

import akka.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller}
import akka.http.scaladsl.model.HttpEntity._
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, MessageEntity, Uri}
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshaller}
import akka.util.ByteString
import io.circe.generic.semiauto
import io.circe.{Encoder, KeyDecoder, KeyEncoder}
import scrawler.model.CrawlingId

import scala.concurrent.{ExecutionContext, Future}

case class NewCrawlingRequest(urls: Seq[Uri])

object NewCrawlingRequest {

  implicit def byteString2String(string: String): ByteString = ByteString(string)

  implicit val crawlingRequestUnmarshaller: FromEntityUnmarshaller[NewCrawlingRequest] = {

    def fromHttpEntity2NewCrawlingRequest(httpEntity: HttpEntity): NewCrawlingRequest = {
      new NewCrawlingRequest(Nil)
    }

    def f(ec: ExecutionContext): HttpEntity => Future[NewCrawlingRequest] = {
      httpEntity: HttpEntity => Future {
        fromHttpEntity2NewCrawlingRequest(httpEntity)
      }(ec)
    }

    Unmarshaller(f)
  }

  implicit val crawlingRequestMarshaller: ToEntityMarshaller[NewCrawlingRequest] = {
    def marshal(obj: NewCrawlingRequest): MessageEntity = {
      new Strict(
        contentType = ContentTypes.`application/json`,
        data = "123"
      )
    }
    Marshaller.opaque(marshal)
  }

}

case class NewCrawlingResponse(id: CrawlingId)

case class CrawlingResultResponse(id: CrawlingId, results: Map[Uri, String])
