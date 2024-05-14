package Roulette.db.models

import java.util.UUID
import Roulette.core.{Bet, Player}
import slick.jdbc.PostgresProfile.api._
import slick.lifted.{ProvenShape, Tag}

class BetsTable(tag: Tag) extends Table[Bet](tag, "bets") {
  def id = column[Option[UUID]]("id", O.PrimaryKey, O.AutoInc)
  def betType = column[Option[String]]("bet_type")
  def playerID = column[Option[UUID]]("player_id")
  def betNumber = column[Option[Int]]("bet_number")
  def betOddOrEven = column[Option[String]]("bet_odd_or_even")
  def betColor = column[Option[String]]("bet_color")
  def betAmount = column[Option[Int]]("bet_amount")
  def randomNumber = column[Option[Int]]("random_number")

  // Match tuple to the case class exactly, including the order of parameters
  def * = (id, betType, playerID, betNumber, betOddOrEven, betColor, betAmount, randomNumber) <> ((Bet.apply _).tupled, Bet.unapply)
}

class PlayersTable(tag: Tag) extends Table[Player](tag, "players") {
  def id = column[UUID]("id", O.PrimaryKey)
  def availableMoney = column[Int]("available_money")

  // Ensure this matches the case class constructor exactly
  def * : ProvenShape[Player] = (id, availableMoney) <> ((Player.mapperTo _).tupled, Player.unapply)
}
