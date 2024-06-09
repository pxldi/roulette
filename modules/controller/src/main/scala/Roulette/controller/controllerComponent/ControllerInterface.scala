package Roulette.controller.controllerComponent

import Roulette.core.{Bet, Player}
import Roulette.controller.controllerComponent.State.*
import Roulette.utility.Observable
import akka.Done

import scala.concurrent.Future

trait ControllerInterface extends Observable:
  var randomNumber: Int
  def setupPlayers(): Unit
  def updatePlayer(playerIndex: Int, money: Int): Unit
  def setPlayers(players: Vector[Player]): Unit
  def setBets(bets: Vector[Bet]): Unit
  def getPlayers: Vector[Player]
  def getBets: Vector[Bet]
  def changeMoney(playerIndex: Int, money: Int, add: Boolean): Unit
  def checkGameEnd(): Unit
  def undo(): Unit
  def redo(): Unit
  def save(): Unit
  def load(): Unit
  def saveToDb(): Future[Unit]
  def loadFromDb(): Future[Unit]
  def quit(): Unit
  def createAndAddBet(playerIndex: Int, betType: String, value: Option[Int], oddOrEven: Option[String], color: Option[String], betAmount: Int): Future[Boolean]
  def addBet(bet: Bet): Future[Boolean]
  def calculateBets(): Vector[String]
  def calculateBetsStreamKafka(): Future[Done]
  def calculateBetsStream(): Future[Vector[String]]
  def generateRandomNumber(): Unit
  def winBet(playerIndex: Int, bet: Int, winRate: Int, rouletteNumber: Int): String
  def loseBet(playerIndex: Int, bet: Int, rouletteNumber: Int): String
  def changeState(state: State): Unit
  def getState: State
  def printState(): String
  def num(bet: Bet): String
  def evenOdd(bet: Bet): String
  def color(bet: Bet): String
