package scrawler.model

import akka.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller}
import akka.http.scaladsl.model.HttpEntity.Strict
import akka.http.scaladsl.model._
import akka.util.ByteString

sealed trait CrawlingError
case object NoSuchCrawlingRequest extends CrawlingError

case class CrawlingId(value: Long) extends AnyVal
//object CrawlingId {
//
//}

//case class CrawlingResult(result: Map[Uri, Either[CrawlingError, String]])
