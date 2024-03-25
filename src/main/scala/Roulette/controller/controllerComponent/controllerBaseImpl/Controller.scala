package Roulette.controller.controllerComponent.controllerBaseImpl

import Roulette.controller.PutCommand
import Roulette.controller.controllerComponent.State.*
import Roulette.controller.controllerComponent.{ControllerInterface, State}
import Roulette.model.fileIOComponent.FileIOInterface
import Roulette.model.{Bet, Player, PlayerUpdate}
import Roulette.util.{Event, Observable, UndoManager}
import Roulette.{controller, util}

import scala.collection.immutable.VectorBuilder
import scala.io.StdIn.readLine
import scala.util.Random

class Controller(using val fIO: FileIOInterface) extends ControllerInterface with Observable {

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

  def getPlayers(): Vector[Player] =
    players

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

  def save(): Unit =
    fIO.save(players, bets)

  def load(): Unit =
    val (vector_players, vector_bets) = fIO.load()
    players = vector_players
    bets = vector_bets
    notifyObservers(Event.UPDATE)

  def quit(): Unit =
    notifyObservers(Event.QUIT)
  
  def addBet(bet: Bet): Boolean = {
    bet.bet_amount match {
      case Some(betAmount) if betAmount > players(bet.player_index.getOrElse(0)).getAvailableMoney =>
        println("Not enough money available to bet that amount!")
        false
      case Some(betAmount) =>
        // Zufallszahl setzen, bevor die Wette hinzugefügt wird
        val updatedBet = bet.copy(random_number = Some(randomNumber))
        bets = bets :+ updatedBet
        changeMoney(bet.player_index.getOrElse(0), betAmount, false)
        true
      case None =>
        println("Bet amount not provided")
        false
    }
  }



  /*def calculateBets(): Vector[String] =
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
    vc.result()*/
  def calculateBets(): Vector[String] = {
    val vc = VectorBuilder[String]()
    for (bet <- bets) {
      println(s"Debug: bet_type = ${bet.bet_type}") // Debugg
      bet.bet_type match {
        case Some("n") =>
          vc.addOne(num(bet))
        case Some("e") =>
          vc.addOne(evenOdd(bet))
        case Some("c") =>
          vc.addOne(color(bet))
        case _ =>
          println("Error: Unknown bet type " + bet.bet_type.getOrElse("unknown"))
      }
    }
    generateRandomNumber()
    bets = Vector[Bet]()
    checkGameEnd()
    vc.result()
  }

  def generateRandomNumber(): Unit =
    randomNumber = r.nextInt(37)

  def getRandomNumber: Int =
    randomNumber

  def winBet(playerIndex: Int, bet: Int, winRate: Int, rouletteNumber: Int): String = {
    val won_money: Int = bet * winRate
    changeMoney(playerIndex, won_money, true)
    val retvalue =
      "Player " + (playerIndex + 1) + " won $" + won_money + " on number " + rouletteNumber +" They now have $" + players(
        playerIndex).getAvailableMoney + " available."
    retvalue
  }

  def loseBet(playerIndex: Int, bet: Int, rouletteNumber: Int): String = {
    val lost_money: Int = bet
    val retval =
      "Player " + (playerIndex + 1) + " lost $" + lost_money + " on number " + rouletteNumber +" They now have $" + players(
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

  // für zahlen wetten
  class NumExpression(bet: Bet) extends Expression {
    def interpret(): String = {
      (bet.random_number, bet.bet_number, bet.player_index, bet.bet_amount) match {
        case (Some(randNum), Some(betNum), Some(playerIdx), Some(betAmount)) if randNum == betNum =>
          winBet(playerIdx, betAmount, 36, randNum)
        case (Some(_), Some(_), Some(playerIdx), Some(betAmount)) =>
          loseBet(playerIdx, betAmount, randomNumber)
        case _ =>
          "Invalid bet configuration"
      }
    }
  }

  // für gerade oder ungerade wetten
  class EOExpression(bet: Bet) extends Expression {
    def interpret(): String = {
      println(s"Debug: bet_odd_or_even = ${bet.bet_odd_or_even}, random_number = ${bet.random_number}, player_index = ${bet.player_index}, bet_amount = ${bet.bet_amount}")
      (bet.bet_odd_or_even, bet.random_number, bet.player_index, bet.bet_amount) match {
        case (Some("o"), Some(randNum), Some(playerIdx), Some(betAmount)) if randNum % 2 != 0 =>
          println(s"Win condition met for odd: randNum = $randNum")
          println("random Number was: " + randomNumber)
          winBet(playerIdx, betAmount, 2, randNum)
        case (Some("e"), Some(randNum), Some(playerIdx), Some(betAmount)) if randNum % 2 == 0 =>
          println(s"Win condition met for even: randNum = $randNum")
          println("random Number was: " + randomNumber)
          winBet(playerIdx, betAmount, 2, randNum)
        case (Some(_), Some(_), Some(playerIdx), Some(betAmount)) =>
          println(s"Lose condition met: randNum = ${bet.random_number.getOrElse("N/A")}")
          println("random Number was: " + randomNumber)
          loseBet(playerIdx, betAmount, bet.random_number.getOrElse(0))
        case _ =>
          "Invalid bet configuration"
      }
    }
  }


  // für Farbenwetten
  class ColorExpression(bet: Bet) extends Expression {
    private val redNumbers = Set(1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36)
    private val blackNumbers = Set(2, 4, 6, 8, 10, 11, 13, 15, 17, 20, 22, 24, 26, 28, 29, 31, 33, 35)

    def interpret(): String = {
      (bet.bet_color, bet.random_number, bet.player_index, bet.bet_amount) match {
        case (Some("r"), Some(randNum), Some(playerIdx), Some(betAmount)) if redNumbers.contains(randNum) =>
          winBet(playerIdx, betAmount, 2, randNum)
        case (Some("b"), Some(randNum), Some(playerIdx), Some(betAmount)) if blackNumbers.contains(randNum) =>
          winBet(playerIdx, betAmount, 2, randNum)
        case (Some(_), Some(_), Some(playerIdx), Some(betAmount)) =>
          loseBet(playerIdx, betAmount, randomNumber)
        case _ =>
          "Invalid bet configuration"
      }
    }
  }
}