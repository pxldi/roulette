package Roulette

import Roulette.aview.TUI
import Roulette.controller.Controller
import Roulette.model.Player

import javax.swing.plaf.TextUI
import scala.io.StdIn.readLine
import scala.util.Random

@main def main(): Unit =
    println("Welcome to Roulette! \n")
    val playerCount : Int = readLine("How many players are playing: " ).toInt
    val player = Player(playerCount)
    val controller = Controller(player)
    val tui = TUI(controller)
