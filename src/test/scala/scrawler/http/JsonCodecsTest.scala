package scrawler.http

import akka.http.scaladsl.model.Uri
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import org.scalatest.FreeSpec
import scrawler.http.JsonCodecs.Decoders._
import scrawler.http.JsonCodecs.Encoders._
import scrawler.model.{CrawlingId, ParsingError}

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

        val value = NewCrawlingRequest(
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
        val value = NewCrawlingResponse(CrawlingId(1234567890))

        assert(
          parse(json).map(_.pretty(printer)) == Right(value.asJson.pretty(printer))
        )

      }
    }

    "CrawlingResultResponse" - {
      "should be encoded correctly" in {
        val json =
          """
            {
             |  "id": 123456,
             |  "attempts": [
             |    {
             |      "url": "http://some.test.url",
             |      "result": {
             |        "Succeed": {
             |          "status": "ok",
             |          "title": "ololo"
             |        }
             |      }
             |    },
             |    {
             |      "url": "http://another.test.url",
             |      "result": {
             |        "Failed": {
             |          "status": "error",
             |          "description": {"ParsingError": "parsing error"}
             |        }
             |      }
             |    },
             |    {
             |      "url": "https://ololo.pyshpysh.ru/popyachso/11",
             |      "result": {
             |        "InProgress": {
             |          "status": "pending"
             |        }
             |      }
             |    }
             |  ]
             |}
          """.stripMargin

        val value = CrawlingResultResponse( CrawlingId(123456),
                                            Seq(
                                              Uri("http://some.test.url")                   -> Some(Right("ololo")),
                                              Uri("http://another.test.url")                -> Some(Left(ParsingError)),
                                              Uri("https://ololo.pyshpysh.ru/popyachso/11") -> None
                                            ))
        assert(
          parse(json).map(_.pretty(printer)) == Right(value.asJson.pretty(printer))
        )

      }
    }
  }

}
