package Roulette

import Roulette.model.Player

import scala.io.StdIn.readLine
import scala.util.Random

@main def main(): Unit =
    println("Roulette \n")
    val playercount : Int = readLine("Anzahl Spieler: " ).toInt
    println("Spieleranzahl: " + playercount)
    Player(playercount)
    //val controller = new Controller(Player(playercount))
//val tui = new TUI(controller)


