package Roulette.controller

import main.scala.Roulette.controller.Controller

class controllerSpec extends WordSpec with Matchers {
  "A controller" should{
    val player = Player(2)
    val controller = Controller(player)
    "have return value in stateToString" in {
      controller.stateToString() should be("Test Update")
    }
  }
}