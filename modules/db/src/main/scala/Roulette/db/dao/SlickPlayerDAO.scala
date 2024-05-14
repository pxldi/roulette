package Roulette.db.dao

import Roulette.core.Player

import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.PostgresProfile.api.*

import java.util.UUID
import scala.language.postfixOps

class SlickPlayerDAO(db: Database)(implicit ec: ExecutionContext) extends PlayerDAO {
  class PlayersTable(tag: Tag) extends Table[Player](tag, "players") {
    def id = column[UUID]("id", O.PrimaryKey)
    def availableMoney = column[Int]("available_money")

    def * = (id, availableMoney) <> (Player.mapperTo tupled, Player.unapply)
  }

  val players = TableQuery[PlayersTable]

  override def create(player: Player): Future[Player] = db.run {
    (players returning players.map(_.id) into ((player, id) => player.copy(id = id))) += player
  }

  override def update(player: Player): Future[Int] = db.run {
    players.filter(_.id === player.id).update(player)
  }

  override def delete(id: UUID): Future[Int] = db.run {
    players.filter(_.id === id).delete
  }

  override def get(id: UUID): Future[Option[Player]] = db.run {
    players.filter(_.id === id).result.headOption
  }

  override def getAll: Future[Vector[Player]] = db.run {
    players.result.map(_.toVector)
  }
}
