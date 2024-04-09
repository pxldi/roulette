package Roulette.core

// Converted to a case class for immutability and providing a copy method
// Fields are defined as val instead of var
case class Bet(
                // Using Option to explicitly handle the absence of values.
                bet_type: Option[String] = Some(" "),
                player_index: Option[Int] = Some(0),
                bet_number: Option[Int] = Some(0),
                bet_odd_or_even: Option[String] = Some(" "),
                bet_color: Option[String] = Some(" "),
                bet_amount: Option[Int] = Some(0),
                random_number: Option[Int] = Some(0)
              ) {
  // Methods for creating modified copies of the instance, supporting the fluent interface pattern.
  def withBetType(bet_type: String): Bet = this.copy(bet_type = Some(bet_type))
  def withPlayerIndex(player_index: Int): Bet = this.copy(player_index = Some(player_index))
  def withBetNumber(bet_number: Int): Bet = this.copy(bet_number = Some(bet_number))
  def withRandomNumber(random_number: Int): Bet = this.copy(random_number = Some(random_number))
  def withOddOrEven(bet_odd_or_even: String): Bet = this.copy(bet_odd_or_even = Some(bet_odd_or_even))
  def withColor(bet_color: String): Bet = this.copy(bet_color = Some(bet_color))
  def withBetAmount(bet_amount: Int): Bet = this.copy(bet_amount = Some(bet_amount))
}
