package Roulette.db.dao

import Roulette.core.Bet
import slick.jdbc.PostgresProfile.api.*

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps
import scala.util.{Failure, Success}

class SlickBetDAO(db: Database)(implicit ec: ExecutionContext) extends BetDAO {
  class BetsTable(tag: Tag) extends Table[Bet](tag, "bets") {
    def id = column[Option[UUID]]("id", O.PrimaryKey, O.Default(Some(UUID.randomUUID())))
    def betType = column[Option[String]]("bet_type")
    def playerID = column[Option[UUID]]("player_id")
    def betNumber = column[Option[Int]]("bet_number")
    def betOddOrEven = column[Option[String]]("bet_odd_or_even")
    def betColor = column[Option[String]]("bet_color")
    def betAmount = column[Option[Int]]("bet_amount")
    def randomNumber = column[Option[Int]]("random_number")

    def * = (id, betType, playerID, betNumber, betOddOrEven, betColor, betAmount, randomNumber) <> (Bet.mapperTo tupled, Bet.unapply)
  }

  val bets = TableQuery[BetsTable]

  override def save(bet: Bet): Future[Bet] = {
    println(s"Attempting to save bet: $bet")
    db.run {
      (bets returning bets.map(_.id) into ((bet, id) => bet.copy(id = id))) += bet
    }.andThen {
      case Success(savedBet) => println(s"Successfully saved bet: $savedBet")
      case Failure(exception) => println(s"Failed to save bet: ${exception.getMessage}")
    }
  }

  override def getAll: Future[Vector[Bet]] = db.run {
    bets.result.map(_.toVector)
  }

  override def deleteAll(): Future[Int] = db.run {
    bets.delete
  }
}
