package Roulette.controller

import Roulette.controller
import Roulette.model.{Player, PlayerBuilder}
import Roulette.util.Observable

import scala.io.StdIn.readLine

class Controller(val player: Player) extends Observable {

  var gameState: GameState = null

  def setupGameState(): Unit = {
    changeGameState(new BetGameState(this))
  }

  def changeGameState(gameState: GameState): Unit = {
    this.gameState = gameState
  }

  def actualPlayer(playerIndex: Int): String = {
    val retval = "Turn of player " + playerIndex + "\n" +
      "Money in the bank: $" + player.players(playerIndex) + "\n"
    notifyObservers
    retval
  }

  def result(randomNumber: Int): String = {
    val retval = "The spin result is: " + randomNumber + ". "
    retval
  }

  def win(playerIndex: Int, bet: Int, winRate: Int): String = {
    val wonMoney: Int = bet * winRate
    player.players(playerIndex) = player.players(playerIndex) + wonMoney
    val retvalue = "You won " + wonMoney + " . You now have $" + player.players(playerIndex) + " in the bank."
    notifyObservers
    retvalue
  }

  def lose(playerIndex: Int, bet: Int): String = {
    player.players(playerIndex) = player.players(playerIndex) - bet
    val retval = "You lost your bet of $" + bet + ". You now have $" + player.players(playerIndex) + " in the bank."
    notifyObservers
    retval
  }

  def getPlayerCount(): Int = {
    player.playerCount
  }

  def stateToString(): String = {
    val retval = "Test Update"
    retval
   }

  def num(tempPlayer: PlayerBuilder): String = {
    NumExpression(tempPlayer).interpret()
  }

  def evenOdd(tempPlayer: PlayerBuilder): String = {
    EOExpression(tempPlayer).interpret()
  }

  def colour(tempPlayer: PlayerBuilder): String = {
    ColourExpression(tempPlayer).interpret()
  }

  trait Expression {
    def interpret(): String
  }

  class NumExpression(player: PlayerBuilder) extends Expression {
    var retval = ""

    def interpret(): String = {
      val num = readLine("On which number do you want to place your bet? (0-36) ").toInt

      retval = retval.concat(result(player.randomNumber))
      if (player.randomNumber == num)
        retval = retval.concat(win(player.playerIndex, player.bet, 36))
      else
        retval = retval.concat(lose(player.playerIndex, player.bet))
      retval
    }
  }

  class EOExpression(player: PlayerBuilder) extends Expression {
    var retval = ""

    def interpret(): String = {
      retval = retval.concat(result(player.randomNumber))
      println("Do you want to bet on odd (o) or even (e) ? ")
      readLine() match
        case "e" =>
          if (player.randomNumber % 2 == 0)
            retval = retval.concat(win(player.playerIndex, player.bet, 2))
          else
            retval = retval.concat(lose(player.playerIndex, player.bet))

        case "o" =>
          if (player.randomNumber % 2 != 0)
            retval = retval.concat(win(player.playerIndex, player.bet, 2))
          else
            retval = retval.concat(lose(player.playerIndex, player.bet))
      retval
    }
  }

  class ColourExpression(player: PlayerBuilder) extends Expression {
    var retval = ""

    var redNumbers = Array(1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36)
    var blackNumbers = Array(2, 4, 6, 8, 10, 11, 13, 15, 17, 20, 22, 24, 26, 28, 29, 31, 33, 35)
    def interpret(): String = {

        retval = retval.concat(result(player.randomNumber))
        println("Do you want to bet on red (r) or black (b) ? ")
        readLine() match
          case "r" =>
            if (redNumbers.contains(player.randomNumber))
              retval = retval.concat(win(player.playerIndex, player.bet, 2))
            else
              retval = retval.concat(lose(player.playerIndex, player.bet))

          case "b" =>
            result(player.randomNumber)
            if (blackNumbers.contains(player.randomNumber))
              retval = retval.concat(win(player.playerIndex, player.bet, 2))
            else
              retval = retval.concat(lose(player.playerIndex, player.bet))
        retval
    }
  }
}
