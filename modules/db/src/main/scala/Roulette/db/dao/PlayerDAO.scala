package Roulette.db.dao

import Roulette.core.Player
import scala.concurrent.Future
import java.util.UUID

trait PlayerDAO {
  def create(player: Player): Future[Player]
  def update(player: Player): Future[Int]
  def delete(id: UUID): Future[Int]
  def get(id: UUID): Future[Option[Player]]
  def getAll: Future[Vector[Player]]
}
