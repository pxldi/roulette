package Roulette

import Roulette.aview.TUI
import Roulette.aview.GUI
import Roulette.controller.Controller
import Roulette.model.Player

import javax.swing.plaf.TextUI
import scala.io.StdIn.readLine
import scala.util.Random

@main def main(): Unit =
    println("Roulette \n")
    val playercount : Int = readLine("Anzahl Spieler: " ).toInt
    val player = Player(playercount)
    val controller = Controller(player)
    val tui = TUI(controller)
    val swingGui = GUI()
