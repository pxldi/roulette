package Roulette.controller.controllerComponent.controllerBaseImpl

import Roulette.controller.PutCommand
import Roulette.controller.controllerComponent.{ControllerInterface, State}
import Roulette.controller.controllerComponent.State.*
import Roulette.fileIO.FileIOInterface
import Roulette.core.{Bet, Player, PlayerUpdate}
import Roulette.db.dao.{BetDAO, PlayerDAO}
import Roulette.utility.{Event, Observable}
import Roulette.controller.UndoManager
import Roulette.{controller, utility}

import scala.collection.immutable.VectorBuilder
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Random, Success}

class Controller(using val fIO: FileIOInterface, val playersDao: PlayerDAO, val betDao: BetDAO)(implicit ec: ExecutionContext)
  extends ControllerInterface with Observable {

  private var state: State = State.IDLE
  private val undoManager: UndoManager = new UndoManager
  private val r = new Random()
  var randomNumber: Int = 0
  var players: Vector[Player] = Vector.empty
  var bets: Vector[Bet] = Vector.empty

  def setupPlayers(): Unit = {
    val initialMoney = 200
    players = Vector.tabulate(2)(index => Player(index, initialMoney))
    players.foreach(player => playersDao.create(player))
    notifyObservers(Event.UPDATE)
  }

  def updatePlayer(playerIndex: Int, money: Int): Unit = {
    players = players.map {
      case player if player.player_index == playerIndex => player.copy(available_money = money)
      case player => player
    }
    notifyObservers(Event.UPDATE)
  }

  def setPlayers(newPlayers: Vector[Player]): Unit = {
    players = newPlayers
    notifyObservers(Event.UPDATE)
  }

  def setBets(newBets: Vector[Bet]): Unit = {
    bets = newBets
    notifyObservers(Event.UPDATE)
  }

  def getBets: Vector[Bet] = bets

  def getPlayers: Vector[Player] = players

  def changeMoney(playerIndex: Int, amount: Int, add: Boolean): Unit = {
    players = players.map {
      case player if player.player_index == playerIndex =>
        val newAmount = if (add) player.available_money + amount else player.available_money - amount
        player.copy(available_money = newAmount)
      case player => player
    }
    notifyObservers(Event.UPDATE)
  }

  def checkGameEnd(): Unit = {
    val moneyValues = players.map(_.available_money)
    val gameEndCondition = moneyValues.exists(_ == 0)
    if (gameEndCondition) {
      players.zipWithIndex.foreach { case (player, index) =>
        if (player.available_money == 0) {
          val event = if (index == 0) Event.P2WIN else Event.P1WIN
          notifyObservers(event)
          setupPlayers()
        }
      }
    }
  }

  def undo(): Unit = {
    undoManager.undoStep()
    notifyObservers(Event.UPDATE)
  }

  def redo(): Unit = {
    undoManager.redoStep()
    notifyObservers(Event.UPDATE)
  }

  def save(): Unit = {
    fIO.save(players, bets)
  }

  def load(): Unit = {
    val (vectorPlayers, vectorBets) = fIO.load()
    players = vectorPlayers
    bets = vectorBets
    notifyObservers(Event.UPDATE)
  }

  def saveToDb(): Future[Unit] = {
    println("Attempting to save players and bets to the database.")

    // First, delete all existing records
    val deletePlayersFuture = playersDao.deleteAll().andThen {
      case Success(_) => println("Successfully deleted all players.")
      case Failure(exception) => println(s"Failed to delete players: ${exception.getMessage}")
    }

    val deleteBetsFuture = betDao.deleteAll().andThen {
      case Success(_) => println("Successfully deleted all bets.")
      case Failure(exception) => println(s"Failed to delete bets: ${exception.getMessage}")
    }

    // Then, save new player records
    val playerSaveFutures = players.map { player =>
      println(s"Saving player: $player")
      playersDao.create(player).andThen {
        case Success(_) => println(s"Successfully saved player: $player")
        case Failure(exception) => println(s"Failed to save player: ${exception.getMessage}")
      }
    }

    // Finally, save new bet records
    val betSaveFutures = bets.map { bet =>
      println(s"Saving bet: $bet")
      betDao.save(bet).andThen {
        case Success(_) => println(s"Successfully saved bet: $bet")
        case Failure(exception) => println(s"Failed to save bet: ${exception.getMessage}")
      }
    }

    // Ensure deletion happens first, then saving records
    for {
      _ <- deletePlayersFuture
      _ <- deleteBetsFuture
      _ <- Future.sequence(playerSaveFutures)
      _ <- Future.sequence(betSaveFutures)
    } yield {
      println("Data saved to database successfully.")
    }
  }

  def loadFromDb(): Future[Unit] = {
    println("Attempting to load players and bets from the database.")

    val loadPlayersFuture = playersDao.getAll.andThen {
      case Success(players) =>
        println(s"Successfully loaded players: $players")
      case Failure(exception) =>
        println(s"Failed to load players: ${exception.getMessage}")
    }

    val loadBetsFuture = betDao.getAll.andThen {
      case Success(bets) =>
        println(s"Successfully loaded bets: $bets")
      case Failure(exception) =>
        println(s"Failed to load bets: ${exception.getMessage}")
    }

    for {
      dbPlayers <- loadPlayersFuture
      dbBets <- loadBetsFuture
    } yield {
      println(s"Assigning loaded players and bets to local variables.")
      players = dbPlayers
      bets = dbBets

      println(s"Players: $players")
      println(s"Bets: $bets")

      notifyObservers(Event.UPDATE)
      println("Data loaded from database successfully.")
    }
  }

  def quit(): Unit = {
    notifyObservers(Event.QUIT)
  }

  def createAndAddBet(playerIndex: Int, betType: String, value: Option[Int], oddOrEven: Option[String], color: Option[String], betAmount: Int): Future[Boolean] = {
    // Validate the player index
    if (playerIndex < 0 || playerIndex >= players.length) {
      Future.failed(new IllegalArgumentException(s"Player with index $playerIndex not found"))
    } else {
      val bet = Bet(
        player_index = Some(playerIndex),
        bet_type = Some(betType),
        bet_number = value,
        bet_odd_or_even = oddOrEven,
        bet_color = color,
        bet_amount = Some(betAmount),
        random_number = Some(randomNumber)
      )
      addBet(bet)
    }
  }

  def addBet(bet: Bet): Future[Boolean] = Future {
    bet.bet_amount match {
      case Some(betAmount) =>
        bet.player_index match {
          case Some(playerIndex) if betAmount > players.lift(playerIndex).map(_.getAvailableMoney).getOrElse(0) =>
            println("Not enough money available to bet that amount!")
            false
          case Some(playerIndex) =>
            // Set the random number before adding the bet
            val updatedBet = bet.copy(random_number = Some(randomNumber))
            bets = bets :+ updatedBet
            changeMoney(playerIndex, betAmount, false)
            true
          case None =>
            println("Player index not provided")
            false
        }
      case None =>
        println("Bet amount not provided")
        false
    }
  }

  def calculateBets(): Vector[String] = {
    generateRandomNumber()
    val results = bets.map { bet =>
      bet.bet_type match {
        case Some("n") => num(bet)
        case Some("e") => evenOdd(bet)
        case Some("c") => color(bet)
        case _ =>
          s"Error: Unknown bet type ${bet.bet_type.getOrElse("unknown")}"
      }
    }
    checkGameEnd()
    bets = Vector.empty
    results
  }

  def generateRandomNumber(): Unit = {
    randomNumber = r.nextInt(37)
  }

  def getRandomNumber: Int = randomNumber

  def winBet(playerIndex: Int, bet: Int, winRate: Int, rouletteNumber: Int): String = {
    val wonMoney: Int = bet * winRate
    changeMoney(playerIndex, wonMoney, add = true)
    s"${getPlayerLabel(playerIndex)} won $wonMoney on number $rouletteNumber."
  }

  def loseBet(playerIndex: Int, bet: Int, rouletteNumber: Int): String = {
    changeMoney(playerIndex, bet, add = false)
    s"${getPlayerLabel(playerIndex)} lost $bet on number $rouletteNumber."
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
      for {
        randNum <- bet.random_number.toRight("Random number not provided")
        betNum <- bet.bet_number.toRight("Bet number not provided")
        playerIndex <- bet.player_index.toRight("Player index not provided")
        betAmount <- bet.bet_amount.toRight("Bet amount not provided")
      } yield {
        if (randNum == betNum) winBet(playerIndex, betAmount, 35, randNum)
        else loseBet(playerIndex, betAmount, randNum)
      }
    }
  }

  class EOExpression(bet: Bet) extends Expression {
    def interpret(): Either[String, String] = {
      for {
        randNum <- bet.random_number.toRight("Random number not provided")
        playerIndex <- bet.player_index.toRight("Player index not provided")
        betAmount <- bet.bet_amount.toRight("Bet amount not provided")
      } yield {
        (bet.bet_odd_or_even, randNum % 2 == 0) match {
          case (Some("o"), false) | (Some("e"), true) => winBet(playerIndex, betAmount, 2, randNum)
          case (Some("e"), false) | (Some("o"), true) => loseBet(playerIndex, betAmount, randNum)
          case _ => "Invalid bet configuration"
        }
      }
    }
  }

  class ColorExpression(bet: Bet) extends Expression {
    private val redNumbers = Set(1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36)
    private val blackNumbers = Set(2, 4, 6, 8, 10, 11, 13, 15, 17, 20, 22, 24, 26, 28, 29, 31, 33, 35)

    def interpret(): Either[String, String] = {
      for {
        randNum <- bet.random_number.toRight("Random number not provided")
        color <- bet.bet_color.toRight("Bet color not provided")
        playerIndex <- bet.player_index.toRight("Player index not provided")
        betAmount <- bet.bet_amount.toRight("Bet amount not provided")
      } yield {
        if (color == "r" && redNumbers.contains(randNum)) winBet(playerIndex, betAmount, 2, randNum)
        else if (color == "b" && blackNumbers.contains(randNum)) winBet(playerIndex, betAmount, 2, randNum)
        else loseBet(playerIndex, betAmount, randNum)
      }
    }
  }

  private def getPlayerLabel(playerIndex: Int): String = {
    players.lift(playerIndex) match {
      case Some(player) if playerIndex == 0 => "Player 1"
      case Some(player) if playerIndex == 1 => "Player 2"
      case Some(_) => s"Player $playerIndex"
      case None => "Unknown player"
    }
  }
}
