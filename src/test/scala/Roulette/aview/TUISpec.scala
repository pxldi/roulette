package Roulette.aview

import Roulette.aview.tuiComponent.tuiBaseImpl.TUI
import Roulette.controller.controllerComponent.ControllerInterface
import Roulette.controller.controllerComponent.controllerBaseImpl.Controller
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

class TUISpec extends AnyWordSpec with should.Matchers with TypeCheckedTripleEquals {

  val fIO = new FileIO
  given FileIOInterface = fIO
  val controller = new Controller
  given ControllerInterface = controller
  controller.generateRandomNumber()
  controller.setupPlayers()
  val tui: TUI = TUI()

  "TUI" should {

    ""

    "analyze Input and place Bet" in {
      val expected = "Player 1 : Available money: $0\nPlayer 2 : Available money: $200\nYour bet was placed!\r\n"
      val stream = new java.io.ByteArrayOutputStream()
      Console.withOut(stream) {
        tui.analyzeInput("1 n 0 200")
      }
      val actual = stream.toString
      print(stream)
      print(stream.toString())
      stream.toString should be (expected)
    }
    
    

  }

}