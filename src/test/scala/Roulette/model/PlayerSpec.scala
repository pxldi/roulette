package Roulette.controller

import Roulette.controller.controllerComponent.ControllerInterface
import Roulette.controller.controllerComponent.State.{IDLE, State}
import Roulette.controller.controllerComponent.controllerBaseImpl.Controller
import Roulette.model.Bet
import Roulette.model.Player
import Roulette.model.fileIOComponent.FileIOInterface
import Roulette.model.fileIOComponent.xmlImpl.FileIO

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

class PlayerSpec extends AnyWordSpec with should.Matchers with TypeCheckedTripleEquals {

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

  "A Player" should {
    "have a method to get his available Money" in {
      val money = controller.players(0).getAvailableMoney
      val expected = 200
      money should ===(expected)
    }
  }
}