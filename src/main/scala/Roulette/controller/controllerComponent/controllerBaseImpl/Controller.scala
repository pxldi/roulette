package Roulette.controller.controllerComponent.controllerBaseImpl

import Roulette.controller.PutCommand
import Roulette.controller.controllerComponent.State.*
import Roulette.controller.controllerComponent.{ControllerInterface, State}
import Roulette.model.{Bet, Player, PlayerUpdate}
import Roulette.util.{Event, Observable, UndoManager}
import Roulette.{controller, util}

import scala.collection.immutable.VectorBuilder
import scala.io.StdIn.readLine
import scala.util.Random

class Controller() extends ControllerInterface with Observable {

  private var state: State = IDLE
  private val undoManager: UndoManager = new UndoManager
  private val r = new Random()
  var players: Vector[Player] = Vector[Player]()
  var bets: Vector[Bet] = Vector[Bet]()
  var randomNumber: Int = 0

  def setupPlayers(): Unit = {
    val vc = VectorBuilder[Player]
    vc.addOne(Player(200))
    vc.addOne(Player(200))
    players = vc.result()
  }

  def updatePlayer(player_index: Int, money: Int): Unit =
    players = players.updated(player_index, Player(money))

  def changeMoney(player_index: Int, money: Int, add: Boolean): Unit = {
    var updated_money: Int = 0
    if (add)
      updated_money = players(player_index).getAvailableMoney + money
    else
      updated_money = players(player_index).getAvailableMoney - money
    undoManager.doStep(
      new PutCommand(new PlayerUpdate(player_index, updated_money, bets, randomNumber), this))
    notifyObservers(Event.UPDATE)
  }

  def checkGameEnd(): Unit =
    val player_one_money = players(0).getAvailableMoney
    val player_two_money = players(1).getAvailableMoney
    if (player_one_money == 0 && player_two_money == 0)
      notifyObservers(Event.DRAW)
      setupPlayers()
      notifyObservers(Event.UPDATE)
    else if (player_one_money == 0)
      notifyObservers(Event.P2WIN)
      setupPlayers()
      notifyObservers(Event.UPDATE)
    else if (player_two_money == 0)
      notifyObservers(Event.P1WIN)
      setupPlayers()
      notifyObservers(Event.UPDATE)

  def undo(): Unit =
    undoManager.undoStep()
    notifyObservers(Event.UPDATE)

  def redo(): Unit =
    undoManager.redoStep()
    notifyObservers(Event.UPDATE)

  def quit(): Unit =
    notifyObservers(Event.QUIT)

  def addBet(bet: Bet): Boolean =
    if (bet.bet_amount > players(bet.player_index).getAvailableMoney)
      println("Not enough money available to bet that amount!")
      false
    else
      bets = bets :+ bet
      changeMoney(bet.player_index, bet.bet_amount, false)
      true

  def calculateBets(): Vector[String] =
    val vc = VectorBuilder[String]
    for (bet <- bets) {
      bet.bet_type match
        case "n" =>
          vc.addOne(num(bet))
        case "e" =>
          vc.addOne(evenOdd(bet))
        case "c" =>
          vc.addOne(color(bet))
        case _ =>
          println("error: bet type " + bet.bet_type)
    }
    generateRandomNumber()
    bets = Vector[Bet]()
    checkGameEnd()
    vc.result()

  def generateRandomNumber(): Unit =
    randomNumber = r.nextInt(37)

  def getRandomNumber: Int =
    randomNumber

  def winBet(playerIndex: Int, bet: Int, winRate: Int): String = {
    val won_money: Int = bet * winRate
    changeMoney(playerIndex, won_money, true)
    val retvalue =
      "Player " + (playerIndex + 1) + " won their bet of $" + won_money + ". They now have $" + players(
        playerIndex).getAvailableMoney + " available."
    retvalue
  }

  def loseBet(playerIndex: Int, bet: Int): String = {
    val lost_money: Int = bet
    val retval =
      "Player " + (playerIndex + 1) + " lost their bet of $" + lost_money + ". They now have $" + players(
        playerIndex).getAvailableMoney + " available."
    retval
  }

  def changeState(state: State): Unit = {
    this.state = state
  }

  def getState: State = {
    this.state
  }

  def printState(): String = {
    State.printState(state)
  }

  def num(bet: Bet): String = {
    NumExpression(bet).interpret()
  }

  def evenOdd(bet: Bet): String = {
    EOExpression(bet).interpret()
  }

  def color(bet: Bet): String = {
    ColorExpression(bet).interpret()
  }

  // Interpreter Pattern
  trait Expression {
    def interpret(): String
  }

  class NumExpression(bet: Bet) extends Expression {
    var retval = ""

    def interpret(): String = {
      if (randomNumber == bet.bet_number)
        retval = retval.concat(winBet(bet.player_index, bet.bet_amount, 36))
      else
        retval = retval.concat(loseBet(bet.player_index, bet.bet_amount))
      retval
    }
  }

  class EOExpression(bet: Bet) extends Expression {
    var retval = ""

    def interpret(): String = {
      bet.bet_odd_or_even match
        case "o" =>
          if (randomNumber % 2 != 0)
            retval = retval.concat(winBet(bet.player_index, bet.bet_amount, 2))
          else
            retval = retval.concat(loseBet(bet.player_index, bet.bet_amount))
        case "e" =>
          if (randomNumber % 2 == 0)
            retval = retval.concat(winBet(bet.player_index, bet.bet_amount, 2))
          else
            retval = retval.concat(loseBet(bet.player_index, bet.bet_amount))
      retval
    }
  }

  class ColorExpression(bet: Bet) extends Expression {
    var retval = ""

    private val redNumbers: Array[Int] =
      Array(1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36)
    private val blackNumbers: Array[Int] =
      Array(2, 4, 6, 8, 10, 11, 13, 15, 17, 20, 22, 24, 26, 28, 29, 31, 33, 35)
    def interpret(): String = {
      bet.bet_color match
        case "r" =>
          if (redNumbers.contains(randomNumber))
            retval = retval.concat(winBet(bet.player_index, bet.bet_amount, 2))
          else
            retval = retval.concat(loseBet(bet.player_index, bet.bet_amount))

        case "b" =>
          if (blackNumbers.contains(randomNumber))
            retval = retval.concat(winBet(bet.player_index, bet.bet_amount, 2))
          else
            retval = retval.concat(loseBet(bet.player_index, bet.bet_amount))
      retval
    }
  }
}
