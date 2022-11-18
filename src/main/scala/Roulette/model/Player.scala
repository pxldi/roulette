package Roulette.model

import scala.io.StdIn.readLine

case class Player(val playerCount: Int) {
  var players = new Array[Int](playerCount)
  playerStartingMoney(players)
  
  def playerStartingMoney(players : Array[Int]): Array[Int] = {
    for (playerIndex <- 0 until playerCount)
      val money: Int = readLine("Player " + playerIndex + ": Starting money: $").toInt
      players(playerIndex) = money
      
    for (playerIndex <- 0 until playerCount)
      println("Player " + playerIndex + ": Starting money: $" + players(playerIndex))
    players
  }
  players

}

// Fluent Interface
class PlayerBuilder:
  var randomNumber: Int = 0
  var playerIndex: Int = 0
  var bet: Int = 0

  def withRandZahl(randomNumber: Int): PlayerBuilder = {
    this.randomNumber = randomNumber
    this
  }

  def withPlayerIndex(playerIndex: Int): PlayerBuilder = {
    this.playerIndex = playerIndex
    this
  }

  def withEinsatz(bet: Int): PlayerBuilder = {
    this.bet = bet
    this
  }
