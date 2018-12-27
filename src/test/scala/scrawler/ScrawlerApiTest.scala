package scrawler

import scala.concurrent.duration._
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}
import scrawler.http.{CrawlingResultResponse, NewCrawlingRequest}
import scrawler.model.{CrawlingId, ResultsStorageImpl}
import scrawler.routing.ScrawlerApiImpl
import scrawler.http.JsonCodecs.Encoders._
import scrawler.http.JsonCodecs.Decoders._
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport._

import scala.concurrent.Await


class ScrawlerApiTest extends WordSpec with Matchers with ScalatestRouteTest {
  val storage = new ResultsStorageImpl()
  val scrApi = new ScrawlerApiImpl(storage)
  val route = scrApi.route

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
          Uri("http://google.com"),
          Uri("http://yandex.com"),
          Uri("http://yandex.com"),
          Uri("http://google.com")
        )
      )
      Post(Uri("/crawlingrequests"), reqEntity) ~> route ~> check {
        assert(entityAs[CrawlingId] match {
          case CrawlingId(id:Long) => true
          case _ => false
        })
      }
    }

    "return ticket results by getting ticketid" in {

     val ticketId = storage.register(
        Seq(Uri("http://google.com"),
            Uri("http://yandex.com"),
            Uri("http://yandex.com"),
            Uri("http://google.com")))

      Thread.sleep(5000)

      Get(Uri(s"/crawlingrequests/${ticketId.value}")) ~> route ~> check {
        entityAs[CrawlingResultResponse] shouldEqual CrawlingResultResponse(ticketId, storage.getResult(ticketId).get)
      }
    }

  }

}
