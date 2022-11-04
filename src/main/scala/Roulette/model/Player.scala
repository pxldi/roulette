package Roulette.model

import scala.io.StdIn.readLine

case class Player(val playerCount: Int) {
  
  var players = new Array[Int](playerCount)
  playerStartMoney(players)
  def playerStartMoney(players : Array[Int]): Array[Int] = {
    for (playerIndex <- 0 until playerCount)
      val money: Int = readLine("Spieler " + playerIndex + " Startkapital: ").toInt
      players(playerIndex) = money
      
    for (playerIndex <- 0 until playerCount)
      println("Spieler " + playerIndex + " Startkapital: " + players(playerIndex))
    players
  }
  players
}
