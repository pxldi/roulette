package Roulette.core

case class Player(player_index: Int, available_money: Int) {
  def getAvailableMoney: Int =
    this.available_money

  override def toString: String = s"Player($player_index, $available_money)"
}

object Player {
  def apply(player_index: Int, available_money: Int): Player = new Player(player_index, available_money)

  def unapply(player: Player): Option[(Int, Int)] = Some((player.player_index, player.available_money))

  def mapperTo(
                player_index: Int,
                available_money: Int
              ): Player = Player(player_index, available_money)
}
