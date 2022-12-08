package Roulette.controllerSpec

import Roulette.controller.controllerComponent.controllerBaseImpl.Controller
import Roulette.controller.controllerComponent.State.*
import Roulette.controller.controllerComponent.State
import Roulette.model.Bet

import scala.io.StdIn.readLine
import scala.collection.immutable.VectorBuilder
import org.scalactic.Equality
import org.scalactic.TolerantNumerics
import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.matchers.should
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.EitherValues
import org.scalactic.TripleEquals.unconstrainedEquality

import scala.Console.in

class ControllerSpec extends AnyWordSpec with should.Matchers with TypeCheckedTripleEquals {
  var state: State = IDLE

  val playerCount: Int = 1
  val startingMoney: Int = 100
  val controller = Controller()
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
      val i = controller.generateRandomNumber()
      val beWithin0and37 = be >= 0 and be <= 37
      i should beWithin0and37
    }
    "have a method to calc a win" in {
      val win = controller.win(0, 20, 2)
      val expected = "Player 1 won their bet of $40. They now have $240 available."
      win should ===(expected)
    }
    "have a method to calc a loose" in {
      val win = controller.lose(0, 20)
      val expected = "Player 1 lost their bet of $20. They now have $240 available."
      win should ===(expected)
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
      val expected = "Player 1 won their bet of $40. They now have $520 available."
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
      val expected = "Player 1 won their bet of $720. They now have $1760 available."
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
      val expected = "Player 1 won their bet of $720. They now have $4240 available."
      evenOdd should ===(expected)
    }
    // TODO Interpreter

  }
}
