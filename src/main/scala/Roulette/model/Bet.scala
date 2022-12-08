package Roulette.model

// Fluent Interface
class Bet {
  var bet_type: String = ""
  var player_index: Int = 0
  var bet_number: Int = 0
  var bet_odd_or_even: String = ""
  var bet_color: String = ""
  var bet_amount: Int = 0

  def withBetType(bet_type: String): Bet = {
    this.bet_type = bet_type
    this
  }

  def withPlayerIndex(player_index: Int): Bet = {
    this.player_index = player_index
    this
  }

  def withBetNumber(bet_number: Int): Bet = {
    this.bet_number = bet_number
    this
  }

  def withOddOrEven(bet_odd_or_even: String): Bet = {
    this.bet_odd_or_even = bet_odd_or_even
    this
  }

  def withColor(bet_color: String): Bet = {
    this.bet_color = bet_color
    this
  }

  def withBetAmount(bet_amount: Int): Bet = {
    this.bet_amount = bet_amount
    this
  }
}
