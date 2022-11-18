package Roulette.controller

import Roulette.controller.Controller
import Roulette.model.Player
import org.scalatest.matchers.should
import org.scalatest.wordspec.AnyWordSpec


class controllerSpec extends AnyWordSpec with should.Matchers {
  "A controller" should{
    val player = Player(2)
    val controller = Controller(player)
    "have return value in stateToString" in {
      controller.stateToString() should be("Test Update")
    }
  }
}