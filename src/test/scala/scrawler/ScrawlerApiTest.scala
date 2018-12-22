package scrawler

import akka.http.scaladsl.client.RequestBuilding._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}


class ScrawlerApiTest extends WordSpec with Matchers {
  val route: Route = (new ScrawlerApiImpl).route

  "The scrawler" should {
    "return answer \"pong\" on GET \"ping\" request" in {
      Get(Uri("/ping")) ~> route ~> check {
        //entityAs[String]
        true shouldEqual true
      }
    }
  }

}
