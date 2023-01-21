package Roulette.model

class Player(available_money: Int) {
  def getAvailableMoney: Int =
    this.available_money

  override def toString: String = available_money.toString
}
