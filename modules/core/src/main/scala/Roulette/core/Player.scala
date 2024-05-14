package Roulette.core

import java.util.UUID

case class Player(id: UUID, available_money: Int) {
  def getAvailableMoney: Int =
    this.available_money

  override def toString: String = s"Player($id, $available_money)"
}

object Player {
  def apply(id: UUID, available_money: Int): Player = new Player(id, available_money)

  def unapply(player: Player): Option[(UUID, Int)] = Some((player.id, player.available_money))

  def mapperTo(
    id: UUID,
    available_money: Int
  ): Player = Player(id, available_money)
}