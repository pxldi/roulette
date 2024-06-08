package Roulette.db.models

import Roulette.core.{Bet, Player}
import slick.jdbc.PostgresProfile.api._
import slick.lifted.{ProvenShape, Tag}

class BetsTable(tag: Tag) extends Table[Bet](tag, "bets") {
  def betType = column[Option[String]]("bet_type")
  def playerIndex = column[Option[Int]]("player_index")
  def betNumber = column[Option[Int]]("bet_number")
  def betOddOrEven = column[Option[String]]("bet_odd_or_even")
  def betColor = column[Option[String]]("bet_color")
  def betAmount = column[Option[Int]]("bet_amount")
  def randomNumber = column[Option[Int]]("random_number")

  // Match tuple to the case class exactly, including the order of parameters
  def * = (betType, playerIndex, betNumber, betOddOrEven, betColor, betAmount, randomNumber) <> ((Bet.apply _).tupled, Bet.unapply)
}

class PlayersTable(tag: Tag) extends Table[Player](tag, "players") {
  def playerIndex = column[Int]("player_index", O.PrimaryKey, O.AutoInc)
  def availableMoney = column[Int]("available_money")

  // Ensure this matches the case class constructor exactly
  def * : ProvenShape[Player] = (playerIndex, availableMoney) <> ((Player.apply _).tupled, Player.unapply)
}
