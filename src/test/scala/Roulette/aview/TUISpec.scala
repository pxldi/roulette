import Roulette.aview.TUI
import org.scalatest.matchers.should
import org.scalatest.wordspec.AnyWordSpec

class TUISpec extends AnyWordSpec with should.Matchers {
  "TUI" should {
    val tui = new TUI

    "print instructions" in {
        val stream = new java.io.ByteArrayOutputStream()
        Console.withOut(stream) {
        tui.processInput("h")
        }
        assert(stream.toString() contains "Welcome to Roulette!\nYou can bet by writing 'b'!")
    }

    "print bet" in {
        val stream = new java.io.ByteArrayOutputStream()
        Console.withOut(stream) {
        tui.processInput("b")
        }
        assert(stream.toString() contains "Bet")
    }

    "print quitting the game" in {
        val stream = new java.io.ByteArrayOutputStream()
        Console.withOut(stream) {
        tui.processInput("q")
        }
        assert(stream.toString() contains "Quitting...")
    }
  }
}