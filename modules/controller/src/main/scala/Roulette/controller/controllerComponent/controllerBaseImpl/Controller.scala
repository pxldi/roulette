package Roulette.controller.controllerComponent.controllerBaseImpl

import Roulette.controller.PutCommand
import Roulette.controller.controllerComponent.{ControllerInterface, State}
import Roulette.controller.controllerComponent.State.*
import Roulette.fileIO.FileIOInterface
import Roulette.core.{Bet, Player, PlayerUpdate}
import Roulette.db.dao.{BetDAO, PlayerDAO}
import Roulette.utility.{Event, Observable, UndoManager}
import Roulette.{controller, utility}

import scala.collection.immutable.VectorBuilder
import scala.io.StdIn.readLine
import scala.util.Random
import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class Controller(using val fIO: FileIOInterface, val playersDao: PlayerDAO, val betDao: BetDAO)(implicit ec: ExecutionContext) extends ControllerInterface with Observable {

  private var state: State = IDLE
  private val undoManager: UndoManager = new UndoManager
  private val r = new Random()
  var randomNumber: Int = 0

  def setupPlayers(): Unit = {
    val initialMoney = 200
    List.fill(2)(UUID.randomUUID()).foreach { id =>
      val player = Player(id, initialMoney)
      playersDao.create(player)
    }
  }

  def updatePlayer(player_id: UUID, money: Int): Unit = {
    playersDao.update(Player(player_id, money))
    notifyObservers(Event.UPDATE)
  }
  
  def getBets(): Future[Vector[Bet]] = {
    betDao.getAll
  }

  def getPlayers(): Future[Vector[Player]] = {
    playersDao.getAll
  }

  def changeMoney(playerId: UUID, amount: Int, add: Boolean): Future[Unit] = {
    playersDao.get(playerId).flatMap {
      case Some(player) =>
        val newAmount = if (add) player.available_money + amount else player.available_money - amount
        playersDao.update(player.copy(available_money = newAmount))
      case None =>
        Future.failed(new Exception("Player not found"))
    }
  }

  def checkGameEnd(): Unit = {
    getPlayers().foreach { players =>
      val moneyValues = players.map(_.available_money)
      val gameEndCondition = moneyValues.forall(_ == 0)
      if (gameEndCondition) {
        notifyObservers(Event.DRAW)
        setupPlayers()
        notifyObservers(Event.UPDATE)
      } else {
        players.foreach { player =>
          if (player.available_money == 0) {
            val event = if (player == players.head) Event.P1WIN else Event.P2WIN
            notifyObservers(event)
            setupPlayers()
            notifyObservers(Event.UPDATE)
          }
        }
      }
    }
  }

  def undo(): Unit =
    undoManager.undoStep()
    notifyObservers(Event.UPDATE)

  def redo(): Unit =
    undoManager.redoStep()
    notifyObservers(Event.UPDATE)

  def save(): Unit = {
    fIO.save(getPlayers(), bets)


    def load(): Unit = {
      val (vector_players, vector_bets) = fIO.load()
      bets = vector_bets
      notifyObservers(Event.UPDATE)
    }

    def quit(): Unit =
      notifyObservers(Event.QUIT)

    def addBet(bet: Bet): Future[Boolean] = {
      bet.bet_amount match {
        case Some(betAmount) =>
          bet.player_index match {
            case Some(playerIndex) =>
              playersDao.get(playerIndex).flatMap {
                case Some(player) if betAmount <= player.getAvailableMoney =>
                  val updatedBet = bet.copy(random_number = Some(randomNumber))
                  for {
                    _ <- betDao.create(updatedBet)
                    _ <- changeMoney(player.id, betAmount, subtract = true)
                  } yield true
                case _ =>
                  println("Not enough money available to bet that amount!")
                  Future.successful(false)
              }
            case None =>
              Future.failed(new IllegalArgumentException("Player index not provided"))
          }
        case None =>
          println("Bet amount not provided")
          Future.successful(false)
      }
    }

    def calculateBets(): Future[Vector[String]] = {
      betDao.getAll().flatMap { bets =>
        val results = bets.map { bet =>
          bet.bet_type match {
            case Some("n") => num(bet)
            case Some("e") => evenOdd(bet)
            case Some("c") => color(bet)
            case _ => Future.successful(s"Error: Unknown bet type ${bet.bet_type.getOrElse("unknown")}")
          }
        }
        Future.sequence(results).flatMap { resultMessages =>
          generateRandomNumber()
          betDao.deleteAll().map(_ => resultMessages)
        }
      }
    }


    def generateRandomNumber(): Unit =
      randomNumber = r.nextInt(37)

    def getRandomNumber: Int =
      randomNumber

    def winBet(playerId: UUID, bet: Int, winRate: Int, rouletteNumber: Int): Future[String] = {
      val wonMoney: Int = bet * winRate
      changeMoney(playerId, wonMoney, add = true).map { _ =>
        s"Player $playerId won $$wonMoney on number $rouletteNumber."
      }
    }

    def loseBet(playerId: UUID, bet: Int, rouletteNumber: Int): Future[String] = {
      changeMoney(playerId, bet, add = false).map { _ =>
        s"Player $playerId lost $$bet on number $rouletteNumber."
      }
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
}