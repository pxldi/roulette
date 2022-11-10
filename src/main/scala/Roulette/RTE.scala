package Roulette

import Roulette.aview.TUI
import Roulette.model.Player
import controller.Controller
import javax.swing.plaf.TextUI
import scala.io.StdIn.readLine
import scala.util.Random

@main def main(): Unit =
    println("Roulette \n")
    val playercount : Int = readLine("Anzahl Spieler: " ).toInt
    val controller = Controller(playercount).put
    val tui = TUI(controller)
    //val tui = TUI(controller)
//val tui = new Roulette.aview.TUI(controller)


