package Roulette.model

import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

class PlayerSpec extends AnyWordSpec{

  "player Count" in {
    val player : Player = Player(2)
    player.players(0) = 100
    player.players(0).toString() should be ("100")
  }
}
