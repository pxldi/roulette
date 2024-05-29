package Roulette.db.dao

import Roulette.core.Bet
import reactivemongo.api.{Cursor, MongoConnection}
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.api.bson.{BSONDocument, BSONDocumentHandler, Macros}

import scala.concurrent.{ExecutionContext, Future}
import java.util.UUID

class MongoDBBetDAO(dbName: String, collName: String)(implicit ec: ExecutionContext, connection: MongoConnection) extends BetDAO {
  private def collection: Future[BSONCollection] = connection.database(dbName).map(_.collection(collName))

  implicit val betHandler: BSONDocumentHandler[Bet] = Macros.handler[Bet]

  def save(bet: Bet): Future[Bet] = {
    collection.flatMap(_.insert.one(bet)).map(_ => bet)
  }

  def getAll: Future[Vector[Bet]] = {
    collection.flatMap(
      _.find(BSONDocument(), projection = None)
        .cursor[Bet]()
        .collect[Vector](maxDocs = -1, err = Cursor.FailOnError[Vector[Bet]]())
    )
  }

  def deleteAll(): Future[Int] = {
    collection.flatMap(_.delete().one(BSONDocument())).map(_.n)
  }
}
