package scrawler

import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}
import scrawler.http.{NewCrawlingRequest, CrawlingResultResponse}
import scrawler.model.ResultsStorageImpl
import scrawler.routing.ScrawlerApiImpl

import scala.concurrent.Await


class ScrawlerApiTest extends WordSpec with Matchers with ScalatestRouteTest {
  val storage = new ResultsStorageImpl()
  val route = (new ScrawlerApiImpl(storage)).route

  "The scrawler" should {
    "return answer \"pong\" on GET \"ping\" request" in {
      Get(Uri("/ping")) ~> route ~> check {
        val response = entityAs[String]
        println(s"Got response: $response")
        response shouldEqual "pong"
      }
    }

    "return ticket id by getting urls list" in {
      val reqEntity = NewCrawlingRequest(
        List(
          "http://google.com",
          "http://yandex.com",
          "http://yandex.com",
          "http://google.com"
        )
      )
      Post(Uri("/crawlingrequests"), reqEntity) ~> route ~> check {
        val response = (entityAs[String]).toLong
        response shouldEqual 12345L
      }
    }

    "return ticket results by getting ticketid" in {

     val ticketId = storage.register(
        Seq(Uri("http://google.com"),
            Uri("http://yandex.com"),
            Uri("http://yandex.com"),
            Uri("http://google.com")))

      Get(Uri(s"crawlingrequests/${ticketId.value}")) ~> route ~> check {
        val response = entityAs[CrawlingResultResponse]
      }
    }

  }

}
