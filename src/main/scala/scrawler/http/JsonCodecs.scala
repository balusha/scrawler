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
    implicit val crawlingResultResponseDecoder: Decoder[CrawlingResultResponse] = deriveDecoder

    implicit val uriDecoder: Decoder[Uri] = (c: HCursor) => c.as[String].map(Uri(_))
    implicit val crawlingId: Decoder[CrawlingId] = deriveUnwrappedDecoder

    implicit val attemptStatusDecoder: Decoder[AttemptStatus] = (c: HCursor) => c.as[String] map {
      case "ok" => AttemptStatus.Ok
      case "pending" => AttemptStatus.Pending
      case "error" => AttemptStatus.Error
    }

    implicit val parsingErrorDecoder: Decoder[ParsingError.type] = (c: HCursor) => c.as[String] map {
      _ => ParsingError
    }
  }

  object Encoders {

    implicit val newCrawlingRequestEncoder: Encoder[NewCrawlingRequest] = deriveEncoder

    implicit val crawlingIdEncoder: Encoder[CrawlingId] = deriveUnwrappedEncoder

    implicit val uriEncoder: Encoder[Uri] = (a: Uri) => Json.fromString(a.toString())

    implicit val uriKeyEncoder: KeyEncoder[Uri] =
      (key: Uri) => key.toString()

    implicit val crawlingResultResponseEncoder: Encoder[CrawlingResultResponse] = deriveEncoder

    implicit val validationRejectionEncoder: Encoder[ValidationRejection] = deriveEncoder

    implicit val throwableEncoder: Encoder[Throwable] = Encoder.instance { x =>
      x.getMessage.asJson
    }

    implicit val attemptStatusEncoder: Encoder[AttemptStatus] = Encoder.instance {
      x => (x match {
        case x@AttemptStatus.Ok=> "ok"
        case x@AttemptStatus.Pending => "pending"
        case x@AttemptStatus.Error => "error"
      }).asJson
    }

    implicit val parsingErrorEncoder: Encoder[ParsingError.type] = new Encoder[ParsingError.type] {
      override def apply(a: ParsingError.type): Json = "parsing error".asJson
    }
  }

}

