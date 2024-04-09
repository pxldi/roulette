package Roulette.aview.tuiComponent.tuiBaseImpl

import Roulette.core.{Bet, Player}
import Roulette.controller.controllerComponent.{ControllerInterface, State}
import Roulette.controller.controllerComponent.controllerBaseImpl.Controller
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
      loop()

  def analyzeInput(input: String): Unit =
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
      case "s" => controller.save(); None
      case "l" => controller.load(); None
      case "q" => controller.quit(); None
      case null => None
      case _ =>
        processBet(input)

  private def processBet(input: String): Option[Bet] =
    input.split(" ").toList match
      case p :: t :: v :: a :: Nil =>
        try {
          val playerIndex = p.toInt - 1
          val betAmount = a.toInt
          val randomNumber = controller.randomNumber
          t match
            case "n" =>
              val betNumber = v.toInt
              Some(Bet(
                player_index = Some(playerIndex),
                bet_type = Some(t),
                bet_number = Some(betNumber),
                bet_amount = Some(betAmount),
                random_number = Some(randomNumber)
              ))
            case "e" | "c" =>
              Some(Bet(
                player_index = Some(playerIndex),
                bet_type = Some(t),
                bet_odd_or_even = if (t == "e") Some(v) else None,
                bet_color = if (t == "c") Some(v) else None,
                bet_amount = Some(betAmount),
                random_number = Some(randomNumber)
              ))
            case _ =>
              println("Invalid bet type.")
              None
        } catch {
          case _: NumberFormatException =>
            println("Please correct your input!")
            None
        }
      case _ =>
        println("Invalid input format.")
        None


  private def convertToInt(p: String, a: String): Try[(Int, Int)] =
    Try(p.toInt - 1, a.toInt)
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
