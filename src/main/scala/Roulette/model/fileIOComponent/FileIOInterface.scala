package Roulette.model.fileIOComponent

import Roulette.model.{Bet, Player}

trait FileIOInterface {
  def save(vector_player: Vector[Player], vector_bet: Vector[Bet]): Unit
  def load(): (Vector[Player], Vector[Bet])
}
