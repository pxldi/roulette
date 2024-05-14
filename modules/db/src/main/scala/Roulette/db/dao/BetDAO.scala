package Roulette.db.dao

import Roulette.core.Bet
import scala.concurrent.Future
import java.util.UUID

trait BetDAO {
  def save(bet: Bet): Future[Bet]
  def getAll: Future[Vector[Bet]]
  def deleteAll(): Future[Int]
}