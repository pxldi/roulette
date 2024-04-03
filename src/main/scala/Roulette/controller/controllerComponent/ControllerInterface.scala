package Roulette.controller.controllerComponent

import Roulette.model.{Bet, Player}
import Roulette.controller.controllerComponent.State.*
import Roulette.util.Observable
trait ControllerInterface extends Observable:
  var randomNumber: Int
  def setupPlayers(): Unit
  def updatePlayer(player_index: Int, money: Int): Unit
  def getPlayers(): Vector[Player]
  def changeMoney(player_index: Int, money: Int, add: Boolean): Unit
  def checkGameEnd(): Unit
  def undo(): Unit
  def redo(): Unit
  def save(): Unit
  def load(): Unit
  def quit(): Unit
  def addBet(bet: Bet): Boolean
  def calculateBets(): Vector[String]
  def generateRandomNumber(): Unit
  def winBet(playerIndex: Int, bet: Int, winRate: Int, randomNumber: Int): Either[String, String]
  def loseBet(playerIndex: Int, bet: Int, randomNumber: Int): Either[String, String]
  def changeState(state: State): Unit
  def getState: State
  def printState(): String
  def num(bet: Bet): String
  def evenOdd(bet: Bet): String
  def color(bet: Bet): String
