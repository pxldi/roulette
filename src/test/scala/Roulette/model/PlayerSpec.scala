package Roulette.model

import Roulette.model.Player
import org.scalatest.matchers.should
import org.scalatest.wordspec.AnyWordSpec
import org.scalactic.TypeCheckedTripleEquals

class PlayerSpec extends AnyWordSpec with should.Matchers with TypeCheckedTripleEquals {
  "A player" should{
    "have player array" in {
      var actual = new Array[Int](playerCount)
      var expected = new Array[Int](playerCount)
      actual should ===(expected)
    }
  }
}