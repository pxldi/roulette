package Roulette

import Roulette.aview.TUI
import Roulette.model.Player

import javax.swing.plaf.TextUI
import scala.io.StdIn.readLine
import scala.util.Random

@main def main(): Unit =
    println("Roulette \n")
    val playercount : Int = readLine("Anzahl Spieler: " ).toInt
    println("Spieleranzahl: " + playercount)
    //val player : Player = Player(playercount)
    val tui = TUI(Player(playercount))
    //val controller = new Controller(Player(playercount))
//val tui = new Roulette.aview.TUI(controller)


