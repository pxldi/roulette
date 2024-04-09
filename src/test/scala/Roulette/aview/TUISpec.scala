package Roulette.aview

import Roulette.controller.controllerComponent.ControllerInterface
import Roulette.controller.controllerComponent.controllerBaseImpl.Controller

import scala.io.StdIn.readLine
import scala.collection.immutable.VectorBuilder
import org.scalactic.Equality
import org.scalactic.TolerantNumerics
import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.matchers.should
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.EitherValues
import org.scalactic.TripleEquals.unconstrainedEquality

class TUISpec extends AnyWordSpec with should.Matchers with TypeCheckedTripleEquals {

  val fIO = new FileIO
  given FileIOInterface = fIO
  val controller = new Controller
  given ControllerInterface = controller
  controller.generateRandomNumber()
  controller.setupPlayers()
  val tui: TUI = TUI()

  "TUI" should {

    "analyze Input and place Bet" in {
      val expected = "Player 1 : Available money: $0\nPlayer 2 : Available money: $200\nYour bet was placed!\n"
      val stream = new java.io.ByteArrayOutputStream()
      Console.withOut(stream) {
        tui.analyzeInput("1 n 0 200")
      }
      val actual = stream.toString
      actual should be (expected)
    }

    "print TUI state" in {
      val expected = "Player 1 : " + "Available money: $0\nPlayer 2 : Available money: $200\n"
      val stream = new java.io.ByteArrayOutputStream()
      Console.withOut(stream) {
        tui.printTUIState()
      }
      val actual = stream.toString()
      actual should be (expected)
    }

    "print Game Title" in {
      val expected =
          """
          | _____             _      _   _
          ||  __ \           | |    | | | |
          || |__) |___  _   _| | ___| |_| |_ ___
          ||  _  // _ \| | | | |/ _ \ __| __/ _ \
          || | \ \ (_) | |_| | |  __/ |_| ||  __/
          ||_|  \_\___/ \__,_|_|\___|\__|\__\___|
          |""".stripMargin + "\n"
      val stream = new java.io.ByteArrayOutputStream()
      Console.withOut(stream) {
        tui.printGameTitle()
      }
      val actual = stream.toString()
      actual should be(expected)
    }

    "print Instructions" in {
      val expected =
        """
          |Instructions: Type...
          |>>> "[Player number (1 or 2)] [Bet type (n / e / c)] [Bet value (0 - 36 / e or o / r or b)] [bet amount]" to bet.
          |>>> (u) or (r) to undo or redo respectively.
          |>>> (d) to stop the betting phase and spin the wheel.
          |>>> (q) to quit the game.
          |""".stripMargin + "\n"
      val stream = new java.io.ByteArrayOutputStream()
      Console.withOut(stream) {
        tui.printInstructions()
      }
      val actual = stream.toString()
      actual should be(expected)
    }


  }

}