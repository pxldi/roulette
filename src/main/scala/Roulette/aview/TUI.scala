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

  inputLoop()
  val einsatz: Int = 0
  val r: Int = 0
  val randZahl: Int = 0
  def inputLoop(): Unit = { //Interpreter Pattern
    for (playerIndex <- 0 until controller.getPlayerCount()) {
      println()
      print(controller.actualPlayer(playerIndex))

      val einsatz = readLine("Ihr Einsatz: ").toInt
      val r = new Random()
      val randZahl = r.nextInt(37)

      val tempPlayer = new PlayerBuilder
      tempPlayer.withRandZahl(randZahl).withPlayerIndex(playerIndex).withEinsatz(einsatz)

      println()

      println("Willst du eine Zahl (z), gerade oder ungerade (g) oder eine Farbe (f) setzen? \n")

      readLine() match
        case "z" =>
          print(controller.num(tempPlayer))
          println()
        case "g" =>
          print(controller.evenOdd(tempPlayer))
          println()
        case "f" =>
          print(controller.colour(tempPlayer))
          println()
    }
    inputLoop()
  }

  override def update: Unit = println(controller.stateToString())
