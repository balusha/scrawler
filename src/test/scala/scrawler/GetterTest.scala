package scrawler.crawling

import akka.actor.ActorSystem
import akka.http.scaladsl.model.Uri
import akka.stream.ActorMaterializer
import org.scalatest.FreeSpec

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext}

class GetterTest extends FreeSpec{

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher

  val getter = new GetterImpl

  "Getter" - {
    "should get title" - {
      "Akka from akka.io" in {

        val uri = Uri("https://akka.io")
        val gotTitle = Await.result(getter.getTitle(uri), 15 seconds)
        val needTitle = "Akka"

        assert(gotTitle == needTitle)
      }

      "Google from google.com" in {

        val uri = Uri("http://google.com")
        val gotTitle = Await.result(getter.getTitle(uri), 15 seconds)
        val needTitle = "Google"

        assert(gotTitle == needTitle)
      }

      "Yandex from yandex.ru" in {

        val uri = Uri("http://yandex.ru")
        val gotTitle = Await.result(getter.getTitle(uri), 15 seconds)
        val needTitle = "Яндекс"

        assert(gotTitle == needTitle)
      }

      "vk.com from vk.com" in {

        val uri = Uri("http://vk.com")
        val gotTitle = Await.result(getter.getTitle(uri), 15 minutes)
        val needTitle = "vk.com"

        assert(gotTitle == needTitle)
      }
    }
  }
}
