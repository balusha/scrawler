package scrawler.model

import akka.http.scaladsl.model.Uri

import scala.collection.concurrent.TrieMap
import scala.util.Random

trait ResultsStorage {

  type CrawlingResult = Map[Uri, Option[Either[CrawlingError, String]]]

  def getResult(id: CrawlingId): Option[CrawlingResult]
  def register(Uris: Seq[Uri]): CrawlingId

}

class ResultsStorageImpl() extends ResultsStorage {

  private val getNextId: () => CrawlingId = {
    val random = new Random(123L)
    ()=>{
      CrawlingId(random.nextLong())
    }
  }

  val storage = new TrieMap[CrawlingId, CrawlingResult]

  def getResult(id: CrawlingId): Option[CrawlingResult] = {
    storage.get(id)
  }

  def register(Uris: Seq[Uri]): CrawlingId = {
    val id = getNextId()
    storage += (id -> Uris.map((_, None)).toMap)
    id
  }

}