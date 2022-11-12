package Roulette.aview

import Roulette.controller
import Roulette.controller.Controller
import Roulette.model.Player
import Roulette.model.PlayerBuilder
import Roulette.util.Observer

import scala.io.StdIn.readLine
import scala.util.Random

case class TUI(controller: Controller) extends Observer: //player: Player

  controller.add(this)
  val r = new Random()
  inputLoop()
  val bet: Int = 0

  def inputLoop(): Unit = { //Interpreter Pattern
    for (playerIndex <- 0 until controller.getPlayerCount()) {
      println()
      print(controller.actualPlayer(playerIndex))

      val bet = readLine("How much money do you want to bet: ").toInt
      val randomNumber = r.nextInt(37)

      val tempPlayer = new PlayerBuilder
      tempPlayer.withRandZahl(randomNumber).withPlayerIndex(playerIndex).withEinsatz(bet)

      println()

      println("Do you want to place a bet on a number (n), on odd or even (o) or on a color (c)? \n")

      readLine() match
        case "n" =>
          print(controller.num(tempPlayer))
          println()
        case "o" =>
          print(controller.evenOdd(tempPlayer))
          println()
        case "c" =>
          print(controller.colour(tempPlayer))
          println()
    }
    inputLoop()
  }

  override def update: Unit = println(controller.stateToString())
