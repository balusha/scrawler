package scrawler.model

import akka.actor.ActorSystem
import akka.http.scaladsl.model.Uri
import akka.stream.Materializer
import scrawler.crawling.GetterImpl

import scala.Option
import scala.collection.concurrent.TrieMap
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

trait ResultsStorage {

  type CrawlingResult = Seq[(Uri, CrawlingResultPart)]
  type CrawlingResultPart = Option[Either[CrawlingError, String]]

  def getResult(id: CrawlingId): Option[CrawlingResult]
  def register(Uris: Seq[Uri]): CrawlingId

}

class ResultsStorageImpl()
                        (implicit as: ActorSystem, ec: ExecutionContext, mat: Materializer) extends ResultsStorage {

  private val getter = new GetterImpl
  private val storage = new TrieMap[CrawlingId, CrawlingResult]

  private val getNextId: () => CrawlingId = {
    val random = new Random(123L)
    ()=>{
      CrawlingId(random.nextLong())
      CrawlingId(12345)
    }
  }

  private def runCrawling(id: CrawlingId) = {
    Future.sequence {
     for {
       (uri,_) <- storage(id) // TODO this can explode (use get method)
      } yield getter.getTitle(uri).map(uri -> _)
    }.map { result =>
      storage(id) = result.map { case (uri, title) => uri -> Some(Right(title))} //TODO need "left" scenario handling
    }
  }

  def getResult(id: CrawlingId): Option[CrawlingResult] = {
    storage.get(id)
  }

  def register(uris: Seq[Uri]): CrawlingId = {
    val id = getNextId()
    val startingValue: CrawlingResultPart = None
    val results = Seq(uris.map(_-> startingValue):_*)
    storage += (id -> results)
    runCrawling(id)
    id
  }

}