package Roulette.model

import scala.io.StdIn.readLine

case class Player(val playerCount: Int) {

  playerStartMoney(playerCount)

  def playerStartMoney(playerCount: Int): Array[Int] =
    var players = new Array[Int](playerCount)
    for (playerIndex <- 0 until playerCount)
      val money: Int = readLine("Spieler " + playerIndex + " Startkapital: ").toInt
      players(playerIndex) = money
    println(players(1))
    players
}
