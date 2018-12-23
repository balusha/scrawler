package scrawler

import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}
import scrawler.http.NewCrawlingRequest
import scrawler.model.ResultsStorageImpl


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

  }

}
