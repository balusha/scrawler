package scrawler

import akka.http.scaladsl.Http
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import scrawler.model._
import scala.concurrent.{Await, ExecutionContext}
import scala.util.{Failure, Success}

object Scrawler extends App {

  implicit val system: ActorSystem = ActorSystem("ScrawlerServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher

  val storage  = new ResultsStorageImpl()
  val scrawlerApi: ScrawlerApi = new ScrawlerApiImpl(storage)

  Http()
    .bindAndHandle(scrawlerApi.route, interface = "localhost", port = 8080)
    .onComplete {
      case Success(binding) =>
        println(s"Worker API is listening on $binding")
        sys.addShutdownHook {
          println("Received shutdown hook")
          try {
            import scala.concurrent.duration._
            Await.ready(binding.terminate(10 seconds), 15 seconds)
            Await.ready(system.terminate(), 1 minute)
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

