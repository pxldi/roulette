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
        bets = bets :+ bet
        changeMoney(bet.player_index.getOrElse(0), betAmount, false)
        true
      case None =>
        println("Bet amount not provided")
        false
    }
  }

  def calculateBets(): Vector[String] = {
    val vc = VectorBuilder[String]()
    val random_number = r.nextInt(37)
    for (bet <- bets) {
      val updated_bet = bet.copy(random_number=Some(random_number))
      updated_bet.bet_type match {
        case Some("n") =>
          vc.addOne(num(updated_bet))
        case Some("e") =>
          vc.addOne(evenOdd(updated_bet))
        case Some("c") =>
          vc.addOne(color(updated_bet))
        case _ =>
          println("Error: Unknown bet type " + updated_bet.bet_type.getOrElse("unknown"))
      }
    }
    bets = Vector[Bet]()
    checkGameEnd()
    vc.result()
  }

  def winBet(playerIndex: Int, bet: Int, winRate: Int, rouletteNumber: Int): Either[String, String] = {
    val won_money: Int = bet * winRate
    changeMoney(playerIndex, won_money, true)
    Right(s"Player " + (playerIndex + 1) + " won $" + won_money + " on number " + rouletteNumber +" They now have $" + players(
        playerIndex).getAvailableMoney + " available.")
  }

  def loseBet(playerIndex: Int, bet: Int, rouletteNumber: Int): Either[String, String] = {
    val lost_money: Int = bet
    Right(s"Player " + (playerIndex + 1) + " lost $" + lost_money + " on number " + rouletteNumber +" They now have $" + players(
        playerIndex).getAvailableMoney + " available.")
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
    NumExpression(bet).interpret() match {
      case Right(successMessage) => successMessage
      case Left(errorMessage) => errorMessage
    }
  }

  def evenOdd(bet: Bet): String = {
    EOExpression(bet).interpret() match {
      case Right(successMessage) => successMessage
      case Left(errorMessage) => errorMessage
    }
  }

  def color(bet: Bet): String = {
    ColorExpression(bet).interpret() match {
      case Right(successMessage) => successMessage
      case Left(errorMessage) => errorMessage
    }
  }

  // Interpreter Pattern + TWO Track Pattern

  trait Expression {
    def interpret(): Either[String, String]
  }

  class NumExpression(bet: Bet) extends Expression {
    def interpret(): Either[String, String] = {
      //For-Comprehensions zur Entpackung von Monaden
      for {
        randNum <- bet.random_number.toRight("Random number not provided") // Entpackt die Option von random_number falls None -> Left Fehlermeldung
        betNum <- bet.bet_number.toRight("Bet number not provided")
        playerIdx <- bet.player_index.toRight("Player index not provided")
        betAmount <- bet.bet_amount.toRight("Bet amount not provided")
        result <- if (randNum == betNum) winBet(playerIdx, betAmount, 36, randNum) else loseBet(playerIdx, betAmount, randNum)
      } yield result
    }
  }

  // für gerade oder ungerade wetten
  class EOExpression(bet: Bet) extends Expression {
    def interpret(): Either[String, String] = {
      //For-Comprehensions zur Entpackung von Monaden
      for {
        randNum <- bet.random_number.toRight("Random number not provided")
        playerIdx <- bet.player_index.toRight("Player index not provided")
        betAmount <- bet.bet_amount.toRight("Bet amount not provided")
        result <- (bet.bet_odd_or_even, randNum % 2 == 0) match {
          case (Some("o"), false) | (Some("e"), true) => winBet(playerIdx, betAmount, 2, randNum)
          case (Some("e"), false) | (Some("o"), true) => loseBet(playerIdx, betAmount, randNum)
          case _ => Left("Invalid bet configuration")
        }
      } yield result
    }
  }

  // für Farbenwetten
  class ColorExpression(bet: Bet) extends Expression {
    private val redNumbers = Set(1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36)
    private val blackNumbers = Set(2, 4, 6, 8, 10, 11, 13, 15, 17, 20, 22, 24, 26, 28, 29, 31, 33, 35)

    def interpret(): Either[String, String] = {
      //For-Comprehensions zur Entpackung von Monaden
      for {
        randNum <- bet.random_number.toRight("Random number not provided")
        color <- bet.bet_color.toRight("Bet color not provided")
        playerIdx <- bet.player_index.toRight("Player index not provided")
        betAmount <- bet.bet_amount.toRight("Bet amount not provided")
      } yield {
        if (color == "r" && redNumbers.contains(randNum)) winBet(playerIdx, betAmount, 2, randNum).merge
        else if (color == "b" && blackNumbers.contains(randNum)) winBet(playerIdx, betAmount, 2, randNum).merge
        else loseBet(playerIdx, betAmount, randNum).merge
      }
    }
  }
}