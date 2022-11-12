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

// Fluent Interface
class PlayerBuilder:
  var randZahl: Int = 0
  var playerIndex: Int = 0
  var einsatz: Int = 0

  def withRandZahl(randZahl: Int): PlayerBuilder = {
    this.randZahl = randZahl
    this
  }
  def withPlayerIndex(playerIndex: Int): PlayerBuilder = {
    this.playerIndex = playerIndex
    this
  }
  def withEinsatz(einsatz: Int): PlayerBuilder = {
    this.einsatz = einsatz
    this
  }
