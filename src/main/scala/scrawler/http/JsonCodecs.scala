package scrawler.http

import akka.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller}
import akka.http.scaladsl.model.HttpEntity.Strict
import io.circe._
import io.circe.syntax._
import io.circe.generic.extras.semiauto._
import akka.http.scaladsl.model.{ContentTypes, Uri}
import akka.util.ByteString
import scrawler.model._

object JsonCodecs {

  object Decoders {
    implicit val uriDecoder: Decoder[Uri] = (c: HCursor) => c.as[String].map(Uri(_))
  }

  object Encoders {

    implicit val crawlingIdEncoder: Encoder[CrawlingId] = deriveUnwrappedEncoder

    implicit val uriEncoder: Encoder[Uri] = (a: Uri) => Json.fromString(a.toString())

    implicit val uriKeyEncoder: KeyEncoder[Uri] =
      (key: Uri) => key.toString()

    implicit val сrawlingResultResponseEncoder: Encoder[CrawlingResultResponse] = (a: CrawlingResultResponse) => {
        JsonObject(
          "id"      -> a.id.value.asJson,
          "results" -> a.results.map {
            case (url, result) =>
              JsonObject(
                "url"     -> url.asJson,
                "result"  -> result.asJson
              ).asJson
          }.asJson
        ).asJson
      }
    }

//  object Marshallers {
//    implicit val crawlingIdMarshaller: ToEntityMarshaller[CrawlingId] = {
//      def marshal(obj: CrawlingId) = {
//        new Strict(
//          contentType = ContentTypes.`application/json`,
//          data = ByteString("123")
//        )
//      }
//
//      Marshaller.opaque(marshal)
//    }
//  }


}

