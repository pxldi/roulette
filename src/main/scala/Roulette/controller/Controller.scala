package Roulette.controller

import Roulette.controller
import Roulette.model.{Player, PlayerBuilder}
import Roulette.util.Observable

import scala.io.StdIn.readLine

class Controller(val player: Player) extends Observable {

  def actualPlayer(playerIndex: Int): String = {
    var current = "Spieler " + playerIndex + " ist dran: \n" +
      "Ihr Geld " + player.players(playerIndex) + "\n"
    notifyObservers
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
    notifyObservers
    res
  }

  def loose(playerIndex: Int, einsatz: Int): String = {
    player.players(playerIndex) = player.players(playerIndex) - einsatz
    val res = "Sie haben leider nicht gewonnen und ihren Einsatz von " + einsatz + " verloren. Ihr Geld aktuell: " + player.players(playerIndex)
    notifyObservers
    res
  }

  def getPlayerCount(): Int = {
    player.playerCount
  }

  def stateToString(): String = {
    val res = "Test Update"
    res
   }

  def num(tempPlayer: PlayerBuilder): String = {
    NumExpression(tempPlayer).interpret()
  }

  def evenOdd(tempPlayer: PlayerBuilder): String = {
    EOExpression(tempPlayer).interpret()
  }

  def colour(tempPlayer: PlayerBuilder): String = {
    ColourExpression(tempPlayer).interpret()
  }

  trait Expression {
    def interpret(): String
  }

  class NumExpression(player: PlayerBuilder) extends Expression {
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
  }

  class EOExpression(player: PlayerBuilder) extends Expression {
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
  }

  class ColourExpression(player: PlayerBuilder) extends Expression {
    var zurück = ""

    var roteZahlen = Array(1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36)
    var schwarzeZahlen = Array(2, 4, 6, 8, 10, 11, 13, 15, 17, 20, 22, 24, 26, 28, 29, 31, 33, 35)
    def interpret(): String = {

        zurück = zurück.concat(result(player.randZahl))
        println("Willst du auf rot(r) oder schwarz(s) setzten? ")
        readLine() match
          case "r" =>
            if (roteZahlen.contains(player.randZahl))
              zurück = zurück.concat(win(player.playerIndex, player.einsatz, 2))
            else
              zurück = zurück.concat(loose(player.playerIndex, player.einsatz))

          case "s" =>
            result(player.randZahl)
            if (schwarzeZahlen.contains(player.randZahl))
              zurück = zurück.concat(win(player.playerIndex, player.einsatz, 2))
            else
              zurück = zurück.concat(loose(player.playerIndex, player.einsatz))
        zurück
    }
  }
}
