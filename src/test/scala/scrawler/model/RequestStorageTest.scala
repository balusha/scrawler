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
  val crawlId: CrawlingId = rs.register(uris)
  "Storage" - {
    "should return Id " in {
      assert(crawlId match {
        case CrawlingId(id:Long) => true
        case _ => false
      })
    }

    "should then result by Id" in {
      Thread.sleep(5000)
      val result = rs.getResult(crawlId)
      assert( result match {
          case None => false
          case Some(seqRes) => !uris.zip(seqRes).exists{
            case (a,(b,_)) => a != b
          }
        }
      )
      }
    }
}
