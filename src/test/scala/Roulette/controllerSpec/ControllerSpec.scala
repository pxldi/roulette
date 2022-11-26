package Roulette.controller

import Roulette.controller.State.{IDLE, State}
import Roulette.model.{Player, Bet}

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
  
  "Controller" should {
    "setup the players" in {
      val playerCount : Int = 1
      val startingMoney : Int = 100
      val controller = Controller(playerCount, startingMoney)
      controller.setupPlayers()
      val vc = VectorBuilder[Player]
      for (player_index <- 0 until 1) {
        vc.addOne(Player(100))
      }
      val expected = vc.result()
      val result = controller.players
      result.size should be(expected.size)
      result(0).getAvailableMoney() should be(expected(0).getAvailableMoney())
    }

    "have the ability to add money to a player" in {
      val playerCount : Int = 1
      val startingMoney : Int = 100
      val controller = Controller(playerCount, startingMoney)
      controller.setupPlayers()
      controller.updatePlayer(0, 100, true)
      controller.players(0).getAvailableMoney() should be(200)
    }

    "have the ability to subtract money from a player" in {
      val playerCount : Int = 1
      val startingMoney : Int = 100
      val controller = Controller(playerCount, startingMoney)
      controller.setupPlayers()
      controller.updatePlayer(0, 100, false)
      controller.players(0).getAvailableMoney() should be(0)
    }
  }

  "A Controller" should {
    var state: State = IDLE

    val playerCount : Int = 1
    val startingMoney : Int = 100
    val controller = Controller(playerCount, startingMoney)
    val vc = VectorBuilder[Bet]
    val bets = controller.calculateBets(vc.result())
    controller.setupPlayers()

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
    "have a method to get the amount of Players" in {
      val count = controller.getPlayerCount()
      val expected = 1
      count should ===(expected)
    }
    "have a method to get the State" in {
      val state1 = controller.getState()
      state1 should ===(state)
    }
    "have a method to print the State" in {
      val expected = ""
      controller.printState() should ===(expected)
    }

    "have a method to decide between the colours" in {
      val bet = new Bet
      bet.withBetType("c").withRandomNumber(12).withPlayerIndex(0).withBetAmount(20).withColor("r")
      val colour = controller.color(bet)
      val expected = "Player 1 won their bet of $40. They now have $520 available."
      colour should ===(expected)
    }
    "have a method to decide between the number" in {
      val bet = new Bet
      bet.withBetType("n").withRandomNumber(12).withPlayerIndex(0).withBetAmount(20).withBetNumber(12)
      val num = controller.num(bet)
      val expected = "Player 1 won their bet of $720. They now have $1760 available."
      num should ===(expected)
    }
    "have a method to decide between even and odd" in {
      val bet = new Bet
      bet.withBetType("o").withRandomNumber(12).withPlayerIndex(0).withBetAmount(20).withBetNumber(12)
      val evenOdd = controller.num(bet)
      val expected = "Player 1 won their bet of $720. They now have $4240 available."
      evenOdd should ===(expected)
    }

  }
}
