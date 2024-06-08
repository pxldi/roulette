package Roulette.db.dao

import Roulette.core.Bet
import slick.jdbc.PostgresProfile.api.*

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps
import scala.util.{Failure, Success}

class SlickBetDAO(db: Database)(implicit ec: ExecutionContext) extends BetDAO {
  class BetsTable(tag: Tag) extends Table[Bet](tag, "bets") {
    def betType = column[Option[String]]("bet_type")
    def playerIndex = column[Option[Int]]("player_index")
    def betNumber = column[Option[Int]]("bet_number")
    def betOddOrEven = column[Option[String]]("bet_odd_or_even")
    def betColor = column[Option[String]]("bet_color")
    def betAmount = column[Option[Int]]("bet_amount")
    def randomNumber = column[Option[Int]]("random_number")

    def * = (betType, playerIndex, betNumber, betOddOrEven, betColor, betAmount, randomNumber) <> (Bet.apply _ tupled, Bet.unapply)
  }

  val bets = TableQuery[BetsTable]

  override def save(bet: Bet): Future[Bet] = {
    println(s"Attempting to save bet: $bet")
    db.run {
      (bets returning bets.map(_.playerIndex) into ((bet, _) => bet)) += bet
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
