package Roulette.model.fileIOComponent.jsonImpl

import Roulette.Bet
import Roulette.controller.controllerComponent.ControllerInterface
import Roulette.controller.controllerComponent.controllerBaseImpl.Controller
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*

import scala.collection.immutable.VectorBuilder

class FileIOSpec extends AnyWordSpec {
  "Json IO" should {
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
    "after saving load" in {
      controller.save()
      controller.load()
    }
  }
}

