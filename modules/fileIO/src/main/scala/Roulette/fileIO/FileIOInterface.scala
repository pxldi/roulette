package Roulette.fileIO

import Roulette.core.{Bet, Player}

trait FileIOInterface {
  def save(vector_player: Vector[Player], vector_bet: Vector[Bet]): Unit
  def load(): (Vector[Player], Vector[Bet])
}
