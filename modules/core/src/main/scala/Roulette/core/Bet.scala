package Roulette.core

case class Bet(
                bet_type: Option[String] = None,
                player_index: Option[Int] = None,
                bet_number: Option[Int] = None,
                bet_odd_or_even: Option[String] = None,
                bet_color: Option[String] = None,
                bet_amount: Option[Int] = None,
                random_number: Option[Int] = None
              ) {
  def withBetType(bet_type: String): Bet = this.copy(bet_type = Some(bet_type))
  def withPlayerIndex(player_index: Int): Bet = this.copy(player_index = Some(player_index))
  def withBetNumber(bet_number: Int): Bet = this.copy(bet_number = Some(bet_number))
  def withRandomNumber(random_number: Int): Bet = this.copy(random_number = Some(random_number))
  def withOddOrEven(bet_odd_or_even: String): Bet = this.copy(bet_odd_or_even = Some(bet_odd_or_even))
  def withColor(bet_color: String): Bet = this.copy(bet_color = Some(bet_color))
  def withBetAmount(bet_amount: Int): Bet = this.copy(bet_amount = Some(bet_amount))
}

object Bet {
  def mapperTo(
                bet_type: Option[String],
                player_index: Option[Int],
                bet_number: Option[Int],
                bet_odd_or_even: Option[String],
                bet_color: Option[String],
                bet_amount: Option[Int],
                random_number: Option[Int]
              ): Bet = Bet(bet_type, player_index, bet_number, bet_odd_or_even, bet_color, bet_amount, random_number)
}
