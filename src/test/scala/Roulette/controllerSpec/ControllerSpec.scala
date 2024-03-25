package Roulette.controllerSpec

import Roulette.controller.controllerComponent.controllerBaseImpl.Controller
import Roulette.controller.controllerComponent.State.*
import Roulette.controller.controllerComponent.{ControllerInterface, State}
import Roulette.model.Bet
import Roulette.model.fileIOComponent.FileIOInterface
import Roulette.model.fileIOComponent.xmlImpl.FileIO

import scala.io.StdIn.readLine
import scala.collection.immutable.VectorBuilder
import org.scalactic.Equality
import org.scalactic.TolerantNumerics
import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.matchers.should
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.EitherValues
import org.scalactic.TripleEquals.unconstrainedEquality
import org.scalatest.matchers.Matcher
import org.scalatest.matchers.should.Matchers._

import scala.Console.in

class ControllerSpec extends AnyWordSpec with should.Matchers with TypeCheckedTripleEquals {
  var state: State = IDLE

  val playerCount: Int = 1
  val startingMoney: Int = 100
  val fIO = new FileIO
  given FileIOInterface = fIO
  val controller = new Controller
  given ControllerInterface = controller
  controller.generateRandomNumber()
  controller.setupPlayers()
  val vc = VectorBuilder[Bet]
  val bets = controller.calculateBets()
  controller.setupPlayers()

  "A State" should {
    "have a method to print the State" in {
      val st = State.printState(state)
      val expected = ""
      st should ===(expected)
    }
  }

  "A Controller" should {
    "have a method to calculate Bets" in {
      val expected = 100
      for (s <- bets) {
        s should ===(expected)
      }
    }
    "have a method to generate random Numbers" in {
      val i = controller.getRandomNumber
      val beWithin0and37 = be >= 0 and be <= 37
      i should beWithin0and37
    }
    "have a method to calculate a win" in {
      val randomN = 12 // Beispielhafte zufällige Zahl
      val win = controller.winBet(0, 20, 2, randomN)
      val expected = s"Player 1 won their bet of $$40 on number $randomN. They now have $$240 available."
      win should ===(expected)
    }
    "have a method to calculate a loss" in {
      val randomN = 12 // Beispielhafte zufällige Zahl
      val lose = controller.loseBet(0, 20, randomN)
      val expected = s"Player 1 lost their bet of $$20 on number $randomN. They now have $$220 available." // Angenommen, der Startbetrag war 240
      lose should ===(expected)
    }
    "have a method to get the State" in {
      val state1 = controller.getState
      state1 should ===(state)
    }
    "have a method to print the State" in {
      val expected = ""
      controller.printState() should ===(expected)
    }
    "have a method to decide between the colours" in {
      val bet = new Bet
      bet
        .withBetType("c")
        .withRandomNumber(12)
        .withPlayerIndex(0)
        .withBetAmount(20)
        .withColor("r")
      val colour = controller.color(bet)
      val expected = "Player 1 won their bet of $40. They now have $280 available."
      colour should ===(expected)
    }
    "have a method to decide between the number" in {
      val bet = new Bet
      bet
        .withBetType("n")
        .withRandomNumber(12)
        .withPlayerIndex(0)
        .withBetAmount(20)
        .withBetNumber(12)
      val num = controller.num(bet)
      val expected = "Player 1 won their bet of $720. They now have $1000 available."
      num should ===(expected)
    }
    "have a method to decide between even and odd" in {
      val bet = new Bet
      bet
        .withBetType("o")
        .withRandomNumber(12)
        .withPlayerIndex(0)
        .withBetAmount(20)
        .withBetNumber(12)
      val evenOdd = controller.num(bet)
      val expected = "Player 1 won their bet of $720. They now have $1720 available."
      evenOdd should ===(expected)
    }
  }
}
