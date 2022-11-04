package Roulette.aview

//import Roulette.controller.Controller
import Roulette.model.Player
//import Roulette.util.Observer

import scala.io.StdIn.readLine
import scala.util.Random

case class TUI(player: Player) { //extends Observer
  inputLoop()

  def inputLoop(): Unit = {
    for (playerIndex <- 0 until player.playerCount) {
      println()
      println("Spieler " + playerIndex + " ist dran:")
      println("Ihr Geld " + player.players(playerIndex))
      val einsatz: Int = readLine("Ihr Einsatz: ").toInt
      val r = new Random()
      val randZahl: Int = r.nextInt(37)
      println()
      println("Willst du eine Zahl (z), gerade oder ungerade (g), Farbe (f), \n" +
        //"2 Zahlen (2n), 3 Zahlen (3n), 4 Zahlen (4n), 6 Zahlen (6n), \n" +
        "1-18 (1h), 19-36 (2h), 1-12 (12), 13-24 (24), 25-36 (36), \n" +
        "Reihe 1 4 7 X+3 bis 34 (r1), Reihe 2 5 8 X+3,bis 35 (r2), Reihe 3 6 9 12,X+3 bis 36 (r3) setzen?")
      readLine() match
        case "z" =>
          val einsatzZahl: Int = readLine("Auf welches Feld wollen sie ihren Einsatz setzen? ").toInt
          result(randZahl: Int)

          if (randZahl == einsatzZahl)
            win(playerIndex, einsatz, 36)
          else
            loose(playerIndex, einsatz)

        case "g" =>
          evenOdd(randZahl, playerIndex, einsatz)
        case "f" =>
          redBlack(randZahl, playerIndex, einsatz)

        case "1h" =>
          result(randZahl: Int)
          if (randZahl <= 18)
            win(playerIndex, einsatz, 2)
          else
            loose(playerIndex, einsatz)

        case "2h" =>
          result(randZahl: Int)
          if (randZahl <= 36 && randZahl >= 19)
            win(playerIndex, einsatz, 2)
          else
            loose(playerIndex, einsatz)

        case "12" =>
          result(randZahl: Int)
          if (randZahl <= 12)
            win(playerIndex, einsatz, 3)
          else
            loose(playerIndex, einsatz)
        case "24" =>
          result(randZahl: Int)
          if (randZahl <= 24 && randZahl >= 13)
            win(playerIndex, einsatz, 3)
          else
            loose(playerIndex, einsatz)
        case "36" =>
          result(randZahl: Int)
          if (randZahl <= 36 && randZahl >= 25)
            win(playerIndex, einsatz, 3)
          else
            loose(playerIndex, einsatz)
        case "r1" =>
          var r1Zahlen = Array(1, 4, 7, 10, 13, 16, 19, 22, 25, 28, 31, 34)
          result(randZahl: Int)
          if (r1Zahlen.contains(randZahl))
            win(playerIndex, einsatz, 3)
          else
            loose(playerIndex, einsatz)
        case "r2" =>
          var r2Zahlen = Array(2, 5, 8, 11, 14, 17, 20, 23, 26, 29, 32, 35)
          result(randZahl: Int)
          if (r2Zahlen.contains(randZahl))
            win(playerIndex, einsatz, 3)
          else
            loose(playerIndex, einsatz)
        case "r3" =>
          var r3Zahlen = Array(3, 6, 9, 12, 15, 18, 21, 24, 27, 30, 33, 36)
          result(randZahl: Int)
          if (r3Zahlen.contains(randZahl))
            win(playerIndex, einsatz, 3)
          else
            loose(playerIndex, einsatz)
        case "exit" =>
          System.exit(1)
    }
    inputLoop()
  }

  def result(randZahl: Int): Unit = {
    println("Beim Drehen vom Roulette kam eine: " + randZahl + " heraus")
  }

  def win(playerIndex: Int, einsatz: Int, winRate: Int): Unit = {
    val gewinn: Int = einsatz * winRate
    //Controller.setMoney(playerIndex, gewinn)
    player.players(playerIndex) = player.players(playerIndex) + gewinn

    println("Sie haben " + gewinn + " gewonnen")
    println("Ihr Geld aktuell: " + player.players(playerIndex))
  }

  def loose(playerIndex: Int, einsatz: Int): Unit = {
    player.players(playerIndex) = player.players(playerIndex) - einsatz

    println("Sie haben leider nicht gewonnen und ihren Einsatz von " + einsatz + " verloren")
    println("Ihr Geld aktuell: " + player.players(playerIndex))
  }

  def evenOdd(randZahl: Int, playerIndex: Int, einsatz: Int): Unit = {
    println("Willst du auf gerade(g) oder ungerade(u) setzten? ")
    readLine() match
      case "g" =>
        result(randZahl: Int)
        if (randZahl % 2 == 0)
          win(playerIndex, einsatz, 2)
        else
          loose(playerIndex, einsatz)

      case "u" =>
        result(randZahl: Int)
        if (randZahl % 2 != 0)
          win(playerIndex, einsatz, 2)
        else
          loose(playerIndex, einsatz)
  }

  def redBlack(randZahl: Int, playerIndex: Int, einsatz: Int): Unit = {
    var roteZahlen = Array(1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36)
    var schwarzeZahlen = Array(2, 4, 6, 8, 10, 11, 13, 15, 17, 20, 22, 24, 26, 28, 29, 31, 33, 35)

    println("Willst du auf rot(r) oder schwarz(s) setzten? ")
    readLine() match
      case "r" =>
        result(randZahl: Int)
        if (roteZahlen.contains(randZahl))
          win(playerIndex, einsatz, 2)
        else
          loose(playerIndex, einsatz)

      case "s" =>
        result(randZahl: Int)
        if (schwarzeZahlen.contains(randZahl))
          win(playerIndex, einsatz, 2)
        else
          loose(playerIndex, einsatz)
  }
}
