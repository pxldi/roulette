package Roulette.controller.controllerComponent

import Roulette.model.Bet
import Roulette.controller.controllerComponent.State.*
import Roulette.util.Observable
trait ControllerInterface extends Observable:
  def setupPlayers(): Unit

  def updatePlayer(player_index: Int, money: Int): Unit

  def changeMoney(player_index: Int, money: Int, add: Boolean): Unit

  def checkGameEnd(): Unit

  def addBet(bet: Bet): Boolean

  def generateRandomNumber(): Unit

  def winBet(playerIndex: Int, bet: Int, winRate: Int): String

  def loseBet(playerIndex: Int, bet: Int): String

  def changeState(state: State): Unit

  def getState: State

  def printState(): String

  def num(bet: Bet): String

  def evenOdd(bet: Bet): String

  def color(bet: Bet): String
