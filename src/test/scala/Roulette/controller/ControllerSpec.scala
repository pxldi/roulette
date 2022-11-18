package Roulette.controller

import main.scala.Roulette.controller.Controller
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