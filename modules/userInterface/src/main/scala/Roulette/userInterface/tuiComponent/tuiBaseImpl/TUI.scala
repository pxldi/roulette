package Roulette.userInterface.tuiComponent.tuiBaseImpl

import Roulette.controller.controllerComponent.{ControllerInterface, State}
import Roulette.controller.controllerComponent.controllerBaseImpl.Controller
import Roulette.core.{Bet, Player}
import Roulette.utility.{Event, Observer}

import scala.annotation.tailrec
import scala.collection.immutable.VectorBuilder
import scala.io.StdIn.readLine
import scala.util.{Failure, Success, Try}

class TUI()(using controller: ControllerInterface) extends Observer:
  controller.add(this)
  private var exit = false
  override def update(e: Event): Unit =
    e match
      case Event.UPDATE => printTUIState()
      case Event.DRAW => println("The game ended in a draw!")
      case Event.P1WIN => println("The game ended. Player 1 won!")
      case Event.P2WIN => println("The game ended. Player 2 won!")
      case Event.QUIT => exit = true

  def start(): Unit =
    printGameTitle()
    printInstructions()
    printTUIState()
    loop()

  @tailrec
  private def loop(): Unit =
    if (!exit)
      analyzeInput(readLine(">>>"))
      //println("test")
      loop()

  def analyzeInput(input: String): Unit =
    processInput(input)

  private def processInput(input: String): Unit = {
    input match {
      case "d" => print(controller.calculateBets())
      case "u" => controller.undo()
      case "r" => controller.redo()
      case "s" => controller.save()
      case "l" => controller.load()
      case "q" => controller.quit()
      case null =>
      case _ => processBet(input)
    }
  }

  private def processBet(input: String): Unit = {
    val parts = input.split(" ").toList
    parts match {
      case p :: t :: v :: a :: Nil =>
        try {
          val playerIndex = p.toInt - 1
          val betAmount = a.toInt
          val value = if (t == "n") Some(v.toInt) else None
          val oddOrEven = if (t == "e") Some("o") else if (t == "o") Some("e") else None
          val color = if (t == "c") Some(v) else None

          val success = controller.createAndAddBet(playerIndex, t, value, oddOrEven, color, betAmount)
          if (success) {
            println("Your bet was placed!")
          } else {
            println("Failed to place the bet.")
          }

        } catch {
          case _: NumberFormatException =>
            println("Please correct your input!")
        }
      case _ => None
    }
  }

  private def convertToInt(p: String, v: String, a: String): Try[(Int, Int, Int)] =
    Try(p.toInt - 1, v.toInt, a.toInt)

  def printTUIState(): Unit =
    print("Player 1 : " + "Available money: $" + controller.getPlayers()(0).getAvailableMoney + "\n")
    print("Player 2 : " + "Available money: $" + controller.getPlayers()(1).getAvailableMoney + "\n")
    //println("Game State: " + controller.getState)
  def printGameTitle(): Unit =
    println("""
            | _____             _      _   _
            ||  __ \           | |    | | | |
            || |__) |___  _   _| | ___| |_| |_ ___
            ||  _  // _ \| | | | |/ _ \ __| __/ _ \
            || | \ \ (_) | |_| | |  __/ |_| ||  __/
            ||_|  \_\___/ \__,_|_|\___|\__|\__\___|
            |""".stripMargin)

  def printInstructions(): Unit =
    println("""
            |Instructions: Type...
            |>>> "[Player number (1 or 2)] [Bet type (n / e / c)] [Bet value (0 - 36 / e or o / r or b)] [bet amount]" to bet.
            |>>> (u) or (r) to undo or redo respectively.
            |>>> (d) to stop the betting phase and spin the wheel.
            |>>> (q) to quit the game.
            |""".stripMargin)