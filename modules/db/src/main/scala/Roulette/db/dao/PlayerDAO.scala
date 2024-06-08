package Roulette.db.dao

import Roulette.core.Player
import scala.concurrent.Future

trait PlayerDAO {
  def create(player: Player): Future[Player]
  def update(player: Player): Future[Int]
  def delete(playerIndex: Int): Future[Int]
  def get(playerIndex: Int): Future[Option[Player]]
  def getAll: Future[Vector[Player]]
  def deleteAll(): Future[Int]
}
