package Roulette.aview.tuiComponent.tuiBaseImpl

import Roulette.controller.controllerComponent.State
import Roulette.controller.controllerComponent.controllerBaseImpl.Controller
import Roulette.model.{Bet, Player}
import Roulette.util.{Event, Observer}
import jdk.javadoc.internal.doclets.toolkit.util.DocFinder.Input

import scala.annotation.tailrec
import scala.collection.immutable.VectorBuilder
import scala.io.StdIn.readLine
import scala.util.{Failure, Success, Try}

class TUI(controller: Controller) extends Observer:
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
      loop()

  private def analyzeInput(input: String): Unit =
    processInput(input) match
      case Some(bet) =>
        if (controller.addBet(bet))
          println("Your bet was placed!")
      case _ =>

  private def processInput(input: String): Option[Bet] =
    input match
      case "d" => print(controller.calculateBets()); None
      case "u" => controller.undo(); None
      case "r" => controller.redo(); None
      case "q" => controller.quit(); None
      case null => None
      case _ =>
        processBet(input)

  private def processBet(input: String): Option[Bet] =
    val bet = new Bet
    input.split(" ").toList match
      case p :: t :: v :: a :: Nil =>
        t match
          case "n" =>
            convertToInt(p, v, a) match
              case Success(value) =>
                bet
                  .withPlayerIndex(value._1)
                  .withBetType(t)
                  .withBetNumber(value._2)
                  .withBetAmount(value._3)
                Some(bet)
              case Failure(_) => println("Please correct your input!"); None
          case "e" =>
            convertToInt(p, a) match
              case Success(value) =>
                bet
                  .withPlayerIndex(value._1)
                  .withBetType(t)
                  .withOddOrEven(v)
                  .withBetAmount(value._2)
                Some(bet)
              case Failure(_) => println("Please correct your input!"); None
          case "c" =>
            convertToInt(p, a) match
              case Success(value) =>
                bet
                  .withPlayerIndex(value._1)
                  .withBetType(t)
                  .withColor(v)
                  .withBetAmount(value._2)
                Some(bet)
              case Failure(_) => println("Please correct your input!"); None
      case _ => None

  private def convertToInt(p: String, a: String): Try[(Int, Int)] =
    Try(p.toInt - 1, a.toInt)
  private def convertToInt(p: String, v: String, a: String): Try[(Int, Int, Int)] =
    Try(p.toInt - 1, v.toInt, a.toInt)

  private def printTUIState(): Unit =
    println("Player 1 : " + "Available money: $" + controller.players(0).getAvailableMoney)
    println("Player 2 : " + "Available money: $" + controller.players(1).getAvailableMoney)
    println("Game State: " + controller.getState)
  private def printGameTitle(): Unit =
    println("""
            | _____             _      _   _
            ||  __ \           | |    | | | |
            || |__) |___  _   _| | ___| |_| |_ ___
            ||  _  // _ \| | | | |/ _ \ __| __/ _ \
            || | \ \ (_) | |_| | |  __/ |_| ||  __/
            ||_|  \_\___/ \__,_|_|\___|\__|\__\___|
            |""".stripMargin)

  private def printInstructions(): Unit =
    println("""
            |Instructions: Type...
            |>>> "[Player number (1 or 2)] [Bet type (n / e / c)] [Bet value (0 - 36 / e or o / r or b)] [bet amount]" to bet.
            |>>> (u) or (r) to undo or redo respectively.
            |>>> (d) to stop the betting phase and spin the wheel.
            |>>> (q) to quit the game.
            |""".stripMargin)
