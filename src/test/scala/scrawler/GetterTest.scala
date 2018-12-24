package scrawler.crawling

import akka.actor.ActorSystem
import akka.http.scaladsl.model.Uri
import org.scalatest.FreeSpec

class GetterTest extends FreeSpec{

  implicit val as = ActorSystem()

  "Getter" - {
    "should get title Google from google.com" in {
      val getter = new GetterImpl
      val uri = Uri("https://google.com")
      getter.getTitle(uri)
    }
  }
}
