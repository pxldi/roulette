package Roulette.db.dao

import Roulette.core.Player

import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.PostgresProfile.api.*

import scala.language.postfixOps

class SlickPlayerDAO(db: Database)(implicit ec: ExecutionContext) extends PlayerDAO {
  class PlayersTable(tag: Tag) extends Table[Player](tag, "players") {
    def playerIndex = column[Int]("player_index", O.PrimaryKey, O.AutoInc)
    def availableMoney = column[Int]("available_money")

    def * = (playerIndex, availableMoney) <> (Player.apply _ tupled, Player.unapply)
  }

  val players = TableQuery[PlayersTable]

  override def create(player: Player): Future[Player] = db.run {
    (players returning players.map(_.playerIndex) into ((player, playerIndex) => player.copy(player_index = playerIndex))) += player
  }

  override def update(player: Player): Future[Int] = db.run {
    players.filter(_.playerIndex === player.player_index).update(player)
  }

  override def delete(playerIndex: Int): Future[Int] = db.run {
    players.filter(_.playerIndex === playerIndex).delete
  }

  override def get(playerIndex: Int): Future[Option[Player]] = db.run {
    players.filter(_.playerIndex === playerIndex).result.headOption
  }

  override def getAll: Future[Vector[Player]] = db.run {
    players.result.map(_.toVector)
  }

  override def deleteAll(): Future[Int] = db.run {
    players.delete
  }
}
