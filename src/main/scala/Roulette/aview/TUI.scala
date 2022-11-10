package Roulette.aview

//import Roulette.controller.Controller
import Roulette.model.Player
import Roulette.model.PlayerBuilder
//import Roulette.util.Observer

import scala.io.StdIn.readLine
import scala.util.Random

case class TUI(player: Player) { //extends Observer
  inputLoop()
  val einsatz: Int = 0
  val r: Int = 0
  val randZahl: Int = 0
  def inputLoop(): Unit = { //Interpreter Pattern
    for (playerIndex <- 0 until player.playerCount) {
      println()
      print(actualPlayer(playerIndex))

      val einsatz = readLine("Ihr Einsatz: ").toInt
      val r = new Random()
      val randZahl = r.nextInt(37)

      val playerr = new PlayerBuilder
      playerr.withRandZahl(randZahl).withPlayerIndex(playerIndex).withEinsatz(einsatz)

      println()

      println("Willst du eine Zahl (z), gerade oder ungerade (g), Farbe (f), \n")

      readLine() match
        case "z" =>
          print(NumExpression(playerr).interpret())
        case "g" =>
          print(EOExpression(playerr).interpret())
        case "f" =>
          print(ColourExpression(playerr).interpret())
    }
    inputLoop()
  }

  def actualPlayer(playerIndex: Int): String ={
    var current = "Spieler " + playerIndex + " ist dran: \n" +
      "Ihr Geld " + player.players(playerIndex) + "\n"
    current
  }

  def result(randZahl: Int): String = {
    val res = "Beim Drehen vom Roulette kam eine: " + randZahl + " heraus. "
    res
  }

  def win(playerIndex: Int, einsatz: Int, winRate: Int): String = {
    val gewinn: Int = einsatz * winRate
    //Controller.setMoney(playerIndex, gewinn)
    player.players(playerIndex) = player.players(playerIndex) + gewinn
    val res = "Sie haben " + gewinn + " gewonnen. Ihr Geld aktuell: " + player.players(playerIndex)
    res
  }

  def loose(playerIndex: Int, einsatz: Int): String = {
    player.players(playerIndex) = player.players(playerIndex) - einsatz
    val res = "Sie haben leider nicht gewonnen und ihren Einsatz von " + einsatz + " verloren. Ihr Geld aktuell: " + player.players(playerIndex)
    res
  }

  //Interpreter Pattern
  trait Expression {
    def interpret(): String
  }

  class NumExpression(player: PlayerBuilder) extends Expression:
    var zurück = ""

    def interpret(): String = {
      val num = readLine("Auf welches Feld wollen sie ihren Einsatz setzen? ").toInt

      zurück = zurück.concat(result(player.randZahl))
      if (player.randZahl == num)
        zurück = zurück.concat(win(player.playerIndex, player.einsatz, 36))
      else
        zurück = zurück.concat(loose(player.playerIndex, player.einsatz))
      zurück
    }


  class EOExpression(player: PlayerBuilder) extends Expression:
    var zurück = ""

    def interpret(): String = {
      zurück = zurück.concat(result(player.randZahl))
      println("Willst du auf gerade(g) oder ungerade(u) setzten? ")
      readLine() match
        case "g" =>
          if (player.randZahl % 2 == 0)
            zurück = zurück.concat(win(player.playerIndex, player.einsatz, 2))
          else
            zurück = zurück.concat(loose(player.playerIndex, player.einsatz))

        case "u" =>
          if (player.randZahl % 2 != 0)
            zurück = zurück.concat(win(player.playerIndex, player.einsatz, 2))
          else
            zurück = zurück.concat(loose(player.playerIndex, player.einsatz))
      zurück
    }

  class ColourExpression(player: PlayerBuilder) extends Expression:
    var zurück = ""

    var roteZahlen = Array(1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36)
    var schwarzeZahlen = Array(2, 4, 6, 8, 10, 11, 13, 15, 17, 20, 22, 24, 26, 28, 29, 31, 33, 35)

    def interpret(): String = {

      zurück = zurück.concat(result(randZahl))
      println("Willst du auf rot(r) oder schwarz(s) setzten? ")
      readLine() match
        case "r" =>
          if (roteZahlen.contains(player.randZahl))
            zurück = zurück.concat(win(player.playerIndex, player.einsatz, 2))
          else
            zurück = zurück.concat(loose(player.playerIndex, player.einsatz))

        case "s" =>
          result(randZahl)
          if (schwarzeZahlen.contains(randZahl))
            zurück = zurück.concat(win(player.playerIndex, player.einsatz, 2))
          else
            zurück = zurück.concat(loose(player.playerIndex, player.einsatz))
      zurück
    }
}
