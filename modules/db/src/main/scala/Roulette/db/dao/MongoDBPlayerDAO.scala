package Roulette.db.dao

import Roulette.core.Player
import reactivemongo.api.{Cursor, MongoConnection}
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.api.bson.{BSONDocument, BSONDocumentHandler, Macros}

import scala.concurrent.{ExecutionContext, Future}

class MongoDBPlayerDAO(dbName: String, collName: String)(implicit ec: ExecutionContext, connection: MongoConnection) extends PlayerDAO {
  private def collection: Future[BSONCollection] = connection.database(dbName).map(_.collection(collName))

  implicit val playerHandler: BSONDocumentHandler[Player] = Macros.handler[Player]

  def create(player: Player): Future[Player] = {
    collection.flatMap(_.insert.one(player)).map(_ => player)
  }

  def update(player: Player): Future[Int] = {
    val selector = BSONDocument("player_index" -> player.player_index)
    val update = BSONDocument(
      "$set" -> BSONDocument(
        "available_money" -> player.available_money
      )
    )
    collection.flatMap(_.update.one(selector, update)).map(_.nModified)
  }

  def delete(playerIndex: Int): Future[Int] = {
    val selector = BSONDocument("player_index" -> playerIndex)
    collection.flatMap(_.delete().one(selector)).map(_.n)
  }

  def get(playerIndex: Int): Future[Option[Player]] = {
    val selector = BSONDocument("player_index" -> playerIndex)
    collection.flatMap(_.find(selector, Option.empty[Player]).one[Player])
  }

  def getAll: Future[Vector[Player]] = {
    collection.flatMap(
      _.find(BSONDocument(), projection = None)
        .cursor[Player]()
        .collect[Vector](maxDocs = -1, err = Cursor.FailOnError[Vector[Player]]())
    )
  }

  def deleteAll(): Future[Int] = {
    collection.flatMap(_.delete().one(BSONDocument())).map(_.n)
  }
}