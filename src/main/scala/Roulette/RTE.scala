package Roulette

import aview.TUI
import scala.io.StdIn.readLine

object RTE {
    val tui = new TUI
    @main def main(): Unit =
        var input: String = " "
        while(input != "q") {
            input = readLine()
            tui.processInput(input)
        }
}

