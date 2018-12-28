package scrawler

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import scrawler.crawling.GetterImpl
import scrawler.model.ResultsStorageImpl
import scrawler.routing.ScrawlerApiImpl

import scala.concurrent.{Await, ExecutionContext}
import scala.util.{Failure, Success}

object SCrawler extends App {

  implicit val webServerAS: ActorSystem = ActorSystem("SCrawlerServer")
  implicit val webServerMaterializer: ActorMaterializer = ActorMaterializer() // not sure it`s neccessary
  implicit val webServerEC: ExecutionContext = webServerAS.dispatcher

  val getter      = new GetterImpl()
  val storage     = new ResultsStorageImpl(getter)
  val scrawlerApi = new ScrawlerApiImpl(storage)

  Http()
    .bindAndHandle(scrawlerApi.route, interface = "localhost", port = 8080)
    .onComplete {
      case Success(binding) =>
        println(s"Simple crawler is ready and waiting on ${binding.localAddress}")
        sys.addShutdownHook {
          println("Received shutdown hook")
          try {
            import scala.concurrent.duration._
            Await.ready(binding.terminate(10.seconds), 15.seconds)
            Await.ready(webServerAS.terminate(), 1.minute)
          } catch {
            case err: Throwable =>
              println("Worker graceful shutdown failed", err)
          }
        }
      case Failure(err) =>
        println("Worker API cant start", err)
        sys.exit(-1)
    }
}

