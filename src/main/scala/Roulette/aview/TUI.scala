package Roulette.aview

import Roulette.controller
import Roulette.controller.Controller
import Roulette.model.Player
import Roulette.model.PlayerBuilder
import Roulette.util.Observer

import scala.io.StdIn.readLine
import scala.util.Random

case class TUI(player: Player) extends Observer: //player: Player
  override def update = ???

  inputLoop()
  val einsatz: Int = 0
  val r: Int = 0
  val randZahl: Int = 0
  def inputLoop(): Unit = { //Interpreter Pattern
    for (playerIndex <- 0 until player.playerCount) {
      println()
      print(Controller(player).actualPlayer(playerIndex))

      val einsatz = readLine("Ihr Einsatz: ").toInt
      val r = new Random()
      val randZahl = r.nextInt(37)

      val tempPlayer = new PlayerBuilder
      tempPlayer.withRandZahl(randZahl).withPlayerIndex(playerIndex).withEinsatz(einsatz)

      println()

      println("Willst du eine Zahl (z), gerade oder ungerade (g), Farbe (f), \n")

      readLine() match
        case "z" =>
          print(Controller(player).num(tempPlayer))
          println()
        case "g" =>
          print(Controller(player).evenOdd(tempPlayer))
          println()
        case "f" =>
          print(Controller(player).colour(tempPlayer))
          println()
    }
    inputLoop()
  }



