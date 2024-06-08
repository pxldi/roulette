package Roulette.userInterface.tuiComponent.tuiBaseImpl

import Roulette.controller.controllerComponent.{ControllerInterface, State}
import Roulette.core.{Bet, Player}
import Roulette.utility.{Event, Observer}

import scala.annotation.tailrec
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.StdIn.readLine
import scala.util.{Failure, Success}

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
    if !exit then
      analyzeInput(readLine(""))
      loop()

  def analyzeInput(input: String): Unit =
    processInput(input).foreach { betFuture =>
      betFuture.onComplete {
        case Success(success) if success => println("Your bet was placed!")
        case Success(_) => println("Failed to place bet.")
        case Failure(exception) => println(s"Error placing bet: ${exception.getMessage}")
      }
    }

  private def processInput(input: String): Option[Future[Boolean]] =
    input match
      case "d" =>
        Future {
          controller.calculateBets()
        }.onComplete {
          case Success(result) => result.foreach(println)
          case Failure(exception) => println(s"Error calculating bets: ${exception.getMessage}")
        }
        None
      case "u" =>
        controller.undo()
        None
      case "r" =>
        controller.redo()
        None
      case "s" =>
        controller.save()
        None
      case "l" =>
        controller.load()
        None
      case "q" =>
        controller.quit()
        None
      case null =>
        None
      case _ =>
        Some(processBet(input))

  private def processBet(input: String): Future[Boolean] =
    input.split(" ").toList match
      case p :: t :: v :: a :: Nil =>
        try
          val playerIndex = p.toInt - 1
          val betAmount = a.toInt
          val betOpt: Option[Bet] = t match
            case "n" =>
              val betNumber = v.toInt
              Some(Bet(
                bet_type = Some("n"),
                player_index = Some(playerIndex),
                bet_number = Some(betNumber),
                bet_amount = Some(betAmount)
              ))
            case "e" =>
              Some(Bet(
                bet_type = Some("e"),
                player_index = Some(playerIndex),
                bet_odd_or_even = Some(v),
                bet_amount = Some(betAmount)
              ))
            case "c" =>
              Some(Bet(
                bet_type = Some("c"),
                player_index = Some(playerIndex),
                bet_color = Some(v),
                bet_amount = Some(betAmount)
              ))
            case _ =>
              println("Invalid bet type.")
              None
          betOpt match
            case Some(bet) => controller.addBet(bet)
            case None => Future.successful(false)
        catch
          case _: NumberFormatException =>
            println("Please correct your input!")
            Future.successful(false)
      case _ =>
        println("Invalid input format.")
        Future.successful(false)

  def printTUIState(): Unit =
    Future {
      controller.getPlayers
    }.onComplete {
      case Success(players) =>
        players.zipWithIndex.foreach { case (player, index) =>
          println(s"Player ${index + 1} : Available money: ${player.available_money}")
        }
      case Failure(exception) =>
        println(s"Error fetching players: ${exception.getMessage}")
    }

  def printGameTitle(): Unit =
    println(
      """
        | _____             _      _   _
        ||  __ \           | |    | | | |
        || |__) |___  _   _| | ___| |_| |_ ___
        ||  _  // _ \| | | | |/ _ \ __| __/ _ \
        || | \ \ (_) | |_| | |  __/ |_| ||  __/
        ||_|  \_\___/ \__,_|_|\___|\__|\__\___|
        |""".stripMargin)

  def printInstructions(): Unit =
    println(
      """
        |Instructions: Type...
        |>>> "[Player number (1 or 2)] [Bet type (n / e / c)] [Bet value (0 - 36 / e or o / r or b)] [bet amount]" to bet.
        |>>> (u) or (r) to undo or redo respectively.
        |>>> (d) to stop the betting phase and spin the wheel.
        |>>> (q) to quit the game.
        |""".stripMargin)
