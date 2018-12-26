package scrawler.json

import akka.http.scaladsl.model.Uri
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import org.scalatest.FreeSpec
import scrawler.http.{CrawlingResultResponse, NewCrawlingRequest, NewCrawlingResponse}
import scrawler.model.CrawlingId

import scrawler.http.JsonCodecs.Decoders._
import scrawler.http.JsonCodecs.Encoders._

class JsonCodecsTest extends FreeSpec{

  val printer: Printer = Printer.spaces2.copy(dropNullValues = true)

  "JsonDecoders" - {
    "NewCrawlingRequest" - {
      "should be decoded correctly" in {
        val json =
          """
            |{
            |	"urls": [
            |		"http://some.test.url",
            |		"http://another.test.url",
            |		"https://ololo.pyshpysh.ru/popyachso/11",
            |		"https://pots.zoxvachjen.ru"
            |	]
            |}
          """.stripMargin

        val value = new NewCrawlingRequest(
          List(
            Uri("http://some.test.url"),
            Uri("http://another.test.url"),
            Uri("https://ololo.pyshpysh.ru/popyachso/11"),
            Uri("https://pots.zoxvachjen.ru")
          )
        )

        assert(decode[NewCrawlingRequest](json) == Right(value))

      }
    }
  }

  "JsonEncoders" - {
    "NewCrawlingResponse" - {
      "should be encoded correctly" in {
        val json =
          """
            |{
            |	"id": 1234567890
            |}
          """.stripMargin
        val value = new NewCrawlingResponse(CrawlingId(1234567890))

        assert(
          parse(json).map(_.pretty(printer)) == Right(value.asJson.pretty(printer))
        )

      }
    }

    "CrawlingResultResponse" - {
      "should be encoded correctly" in {
        val json =
          """
            |{
            |	"id": 123456,
            |	"results": [
            |		{"url":"http://some.test.url", "result": "ololo"},
            |		{"url":"http://another.test.url", "result": "trololo"},
            |		{"url":"https://ololo.pyshpysh.ru/popyachso/11", "result": "pyshpysh"},
            |		{"url":"https://pots.zoxvachjen.ru", "result": "popyachso"}
            |	]
            |}
          """.stripMargin

        val value = new CrawlingResultResponse(
          new CrawlingId(123456),
          List(
            Uri("http://some.test.url")                   -> Some(Right("ololo")),
            Uri("http://another.test.url")                -> Some(Right("trololo")),
            Uri("https://ololo.pyshpysh.ru/popyachso/11") -> Some(Right("pyshpysh")),
            Uri("https://pots.zoxvachjen.ru")             -> Some(Right("popyachso"))
          )
        )

        assert(
          parse(json).map(_.pretty(printer)) == Right(value.asJson.pretty(printer))
        )

      }
    }
  }

}
