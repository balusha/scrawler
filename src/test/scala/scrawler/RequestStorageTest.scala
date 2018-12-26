package scrawler.model

import akka.actor.ActorSystem
import akka.http.scaladsl.model.Uri
import akka.stream.ActorMaterializer
import org.scalatest.FreeSpec

import scala.concurrent.ExecutionContext

class RequestStorageTest extends FreeSpec{

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher

  val rs = new ResultsStorageImpl()

  val uris = List(
    Uri("http://google.com"),
    Uri("http://yandex.com"),
    Uri("http://yandex.com"),
    Uri("http://google.com")
  )
  "Storage" - {
    "should return Id and then result by Id" in {

      val crawlId = rs.register(uris)

      for {
        resultMap <- rs.getResult(crawlId)
        (uri, result) <- resultMap
      } println(s"$uri: $result")

      Thread.sleep(10000)

      for {
        resultMap <- rs.getResult(crawlId)
        (uri, result) <- resultMap
      } println(s"$uri: $result")

      //TODO finish this test

    }
  }
}
