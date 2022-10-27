package Roulette.model

import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

class PlayerSpec extends AnyWordSpec{

  "player Count" in {
    val player = new Player(2)
    player.playerCount should be (2)
  }
}
