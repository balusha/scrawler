package scrawler.http

import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.server.ValidationRejection
import io.circe.Decoder.Result
import io.circe._
import io.circe.generic.auto._
import io.circe.generic.semiauto._
import io.circe.generic.extras.semiauto.{deriveUnwrappedDecoder, deriveUnwrappedEncoder}
import io.circe.syntax._
import scrawler.model._

object JsonCodecs {

  object Decoders {
    implicit val newCrawlingRequestDecoder: Decoder[NewCrawlingRequest] = deriveDecoder

    implicit val uriDecoder: Decoder[Uri] = (c: HCursor) => c.as[String].map(Uri(_))

    implicit val crawlingResultResponseDecoder: Decoder[CrawlingResultResponse] = new Decoder[CrawlingResultResponse] {
      override def apply(c: HCursor): Result[CrawlingResultResponse] = {
        c.downField("id").as[CrawlingId].map { id =>
          val urls = Seq[(Uri, CrawlingResultResponse.CrawlingResultPart)]()
          new CrawlingResultResponse(id, urls)
        }
      }
    }


    implicit val crawlingId: Decoder[CrawlingId] = deriveUnwrappedDecoder

  }

  object Encoders {

    implicit val newCrawlingRequestEncoder: Encoder[NewCrawlingRequest] = deriveEncoder

    implicit val crawlingIdEncoder: Encoder[CrawlingId] = deriveUnwrappedEncoder

    implicit val uriEncoder: Encoder[Uri] = (a: Uri) => Json.fromString(a.toString())

//     implicit val rightValueEncoder: Encoder[Right[_,_]] = Encoder.instance{
//       case Right(x) => x.asJson
//     }

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

    implicit val validationRejectionEncoder: Encoder[ValidationRejection] = deriveEncoder

    implicit val throwableEncoder: Encoder[Throwable] = Encoder.instance { x =>
      x.getMessage.asJson
    }

    implicit val endpointErrorEncoder: Encoder[EndpointError] = Encoder.instance {
      case x: EndpointError.ParsingError => x.asJson
      case x: EndpointError.InternalServerError => x.asJson
    }

    implicit val a: Encoder[EndpointError.ParsingError] = deriveEncoder
    implicit val b: Encoder[EndpointError.InternalServerError] = deriveEncoder

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

