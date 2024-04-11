package Roulette.model

//import Roulette.controller.State.{IDLE, State}

import Roulette.{Bet, Player}

import scala.io.StdIn.readLine
import scala.collection.immutable.VectorBuilder
import org.scalactic.Equality
import org.scalactic.TolerantNumerics
import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.matchers.should
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.EitherValues
import org.scalactic.TripleEquals.unconstrainedEquality

class BetSpec extends AnyWordSpec with should.Matchers with TypeCheckedTripleEquals {
  "A Bet" should {
    "be constructed empty" in {
      val bet = new Bet
      bet.withBetType("").withPlayerIndex(0).withBetNumber(0).withOddOrEven("").withColor("").withBetAmount(0)
      bet.bet_type should be("")
      bet.random_number should be(0)
      bet.player_index should be(0)
      bet.bet_number should be(0)
      bet.bet_odd_or_even should be("")
      bet.bet_color should be("")
      bet.bet_amount should be(0)
    }
  }
}