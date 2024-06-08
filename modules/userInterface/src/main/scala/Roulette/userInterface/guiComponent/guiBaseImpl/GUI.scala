package Roulette.userInterface.guiComponent.guiBaseImpl

import Roulette.controller.controllerComponent.{ControllerInterface, State}
import Roulette.controller.controllerComponent.controllerBaseImpl.Controller
import Roulette.core.{Bet, Player, PlayerUpdate}
import Roulette.utility.{Event, Observer}

import java.awt.{Dimension, Rectangle}
import scala.collection.immutable.VectorBuilder
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.swing.*
import scala.swing.event.*
import scala.util.{Failure, Success}

class GUI()(using controller: ControllerInterface) extends Frame with Observer {

  private var playerIndices: Vector[Int] = Vector.empty
  private var playerLabels: Map[Int, String] = Map.empty

  // UI components
  private val state_label = new Label("Welcome to Roulette!")
  private val player_one_money = new Label("P1: 0$")
  private val player_two_money = new Label("P2: 0$")
  private val result = new Label("Bet Result")
  private val bet_amount_textfield = new TextField("0", 4)
  private val bet_number_textfield = new TextField("0", 4)

  private var selected_player: Int = playerIndices.headOption.getOrElse(0)
  private var bet_type = "n"
  private var bet_value = "0"
  private var bet_amount = 0

  bet_number_textfield.editable = false
  controller.add(this)

  // Initialize the GUI
  contents = new BoxPanel(Orientation.Vertical) {
    contents += Swing.VStrut(15)

    // Status Text
    contents += new BoxPanel(Orientation.Horizontal) {
      contents += state_label
    }

    contents += Swing.VStrut(15)

    // Result Text
    contents += new BoxPanel(Orientation.Horizontal) {
      contents += result
    }

    contents += Swing.VStrut(15)

    // Money Text
    contents += new BoxPanel(Orientation.Horizontal) {
      contents += player_one_money
      contents += Swing.HStrut(15)
      contents += player_two_money
      border = Swing.TitledBorder(Swing.EtchedBorder(Swing.Raised), "Money")
    }

    contents += Swing.VStrut(15)

    // Player Selection
    contents += new BoxPanel(Orientation.Horizontal) {
      contents += Button("Player 1") {
        if (playerIndices.nonEmpty) selected_player = playerIndices(0)
      }

      contents += Swing.HStrut(10)

      contents += Button("Player 2") {
        if (playerIndices.length > 1) selected_player = playerIndices(1)
      }

      border = Swing.TitledBorder(Swing.EtchedBorder(Swing.Raised), "Choose Player")
    }

    contents += Swing.VStrut(15)

    // Bet Amount
    contents += new BoxPanel(Orientation.Horizontal) {
      contents += bet_amount_textfield
      border = Swing.TitledBorder(Swing.EtchedBorder(Swing.Lowered), "Bet Amount")
    }

    contents += Swing.VStrut(15)

    // Bet Options
    contents += new BoxPanel(Orientation.Horizontal) {
      contents += Button("Number") {
        bet_type = "n"
        bet_number_textfield.editable = true
      }
      contents += Swing.HStrut(2)
      contents += Button("Even") {
        bet_type = "e"
        bet_value = "e"
        bet_number_textfield.editable = false
      }
      contents += Swing.HStrut(2)
      contents += Button("Odd") {
        bet_type = "e"
        bet_value = "o"
        bet_number_textfield.editable = false
      }
      contents += Swing.HStrut(2)
      contents += Button("Black") {
        bet_type = "c"
        bet_value = "b"
        bet_number_textfield.editable = false
      }
      contents += Swing.HStrut(2)
      contents += Button("Red") {
        bet_type = "c"
        bet_value = "r"
        bet_number_textfield.editable = false
      }
    }

    contents += Swing.VStrut(15)

    contents += new BoxPanel(Orientation.Horizontal) {
      contents += bet_number_textfield
      border = Swing.TitledBorder(Swing.EtchedBorder(Swing.Lowered), "Bet Number")
    }

    // Place Bet Button
    contents += new BoxPanel(Orientation.Horizontal) {
      contents += Button("Place Bet") {
        createBet()
      }
      contents += Swing.HStrut(5)
      contents += Button("End Betting Phase") {
        Future {
          controller.calculateBets()
        }.onComplete {
          case Success(resultMessages) =>
            Swing.onEDT {
              result.text = resultMessages.map(replaceIndexWithPlayerLabel).mkString("\n")
            }
          case Failure(exception) =>
            showPopup(s"Error calculating bets: ${exception.getMessage}")
        }
      }
    }

    contents += Swing.VStrut(15)

    // Save and Load Buttons
    contents += new BoxPanel(Orientation.Horizontal) {
      contents += Button("Save to DB") {
        Future {
          controller.saveToDb()
        }.onComplete {
          case Success(_) => showPopup("Data saved to database successfully.")
          case Failure(exception) => showPopup(s"Error saving to database: ${exception.getMessage}")
        }
      }
      contents += Swing.HStrut(5)
      contents += Button("Load from DB") {
        Future {
          controller.loadFromDb()
        }.onComplete {
          case Success(_) => showPopup("Data loaded from database successfully.")
          case Failure(exception) => showPopup(s"Error loading from database: ${exception.getMessage}")
        }
      }
      contents += Swing.HStrut(5)
      contents += Button("Save to File") {
        controller.save()
        showPopup("Data saved to file successfully.")
      }
      contents += Swing.HStrut(5)
      contents += Button("Load from File") {
        controller.load()
        showPopup("Data loaded from file successfully.")
      }
    }

    contents += Swing.VStrut(15)

    // Undo Redo
    contents += new BoxPanel(Orientation.Horizontal) {
      contents += Button("Undo") {
        controller.undo()
      }
      contents += Swing.HStrut(5)
      contents += Button("Redo") {
        controller.redo()
      }
      border = Swing.TitledBorder(Swing.EtchedBorder(Swing.Lowered), "Undo & Redo")
    }
    border = Swing.EmptyBorder(20, 20, 20, 20)
  }

  // Fetch players and update labels
  Future {
    controller.getPlayers
  }.onComplete {
    case Success(players) =>
      Swing.onEDT {
        playerIndices = players.indices.toVector
        playerLabels = Map(
          playerIndices(0) -> "Player 1",
          playerIndices(1) -> "Player 2"
        )
        updateLabels(players)
      }
    case Failure(exception) =>
      Swing.onEDT {
        showPopup(s"Error fetching players: ${exception.getMessage}")
      }
  }

  private def updateLabels(players: Vector[Player]): Unit = {
    if (players.nonEmpty && playerIndices.nonEmpty) {
      val player1 = players.headOption
      val player2 = if (players.length > 1) Some(players(1)) else None

      player_one_money.text = s"P1: ${player1.map(_.available_money).getOrElse(0)}"
      player_two_money.text = s"P2: ${player2.map(_.available_money).getOrElse(0)}"
    }
  }

  private def replaceIndexWithPlayerLabel(message: String): String = {
    playerLabels.foldLeft(message) { case (msg, (index, label)) =>
      msg.replace(index.toString, label)
    }
  }

  private def showPopup(message: String): Unit = {
    val dialog = new Dialog()
    dialog.contents = new Label(message)
    dialog.bounds = Rectangle(200, 100)
    dialog.centerOnScreen()
    dialog.visible = true
  }

  override def update(e: Event): Unit = {
    e match {
      case Event.DRAW =>
        showPopup("The game ended in a draw!")
        Future {
          controller.getPlayers
        }.onComplete {
          case Success(players) => Swing.onEDT { updateLabels(players) }
          case Failure(exception) => Swing.onEDT { showPopup(s"Error fetching players: ${exception.getMessage}") }
        }
      case Event.P1WIN =>
        showPopup("The game ended. Player 1 won!")
        Future {
          controller.getPlayers
        }.onComplete {
          case Success(players) => Swing.onEDT { updateLabels(players) }
          case Failure(exception) => Swing.onEDT { showPopup(s"Error fetching players: ${exception.getMessage}") }
        }
      case Event.P2WIN =>
        showPopup("The game ended. Player 2 won!")
        Future {
          controller.getPlayers
        }.onComplete {
          case Success(players) => Swing.onEDT { updateLabels(players) }
          case Failure(exception) => Swing.onEDT { showPopup(s"Error fetching players: ${exception.getMessage}") }
        }
      case Event.UPDATE =>
        Future {
          controller.getPlayers
        }.onComplete {
          case Success(players) => Swing.onEDT { updateLabels(players) }
          case Failure(exception) => Swing.onEDT { showPopup(s"Error fetching players: ${exception.getMessage}") }
        }
      case Event.QUIT =>
        System.exit(0)
    }
  }

  private def createBet(): Unit = {
    val betAmount = try bet_amount_textfield.text.toInt catch {
      case _: NumberFormatException => println("Invalid bet amount"); return
    }

    val bet = bet_type match {
      case "n" =>
        Bet(
          bet_type = Some("n"),
          player_index = Some(selected_player),
          bet_number = try Some(bet_number_textfield.text.toInt) catch {
            case _: NumberFormatException => None
          },
          bet_amount = Some(betAmount)
        )
      case "e" =>
        Bet(
          bet_type = Some("e"),
          player_index = Some(selected_player),
          bet_odd_or_even = Some(bet_value),
          bet_amount = Some(betAmount)
        )
      case "c" =>
        Bet(
          bet_type = Some("c"),
          player_index = Some(selected_player),
          bet_color = Some(bet_value),
          bet_amount = Some(betAmount)
        )
      case _ =>
        println("Unknown bet type")
        return
    }

    Future {
      controller.createAndAddBet(selected_player,
        bet.bet_type.getOrElse(""),
        bet.bet_number,
        bet.bet_odd_or_even,
        bet.bet_color,
        bet.bet_amount.getOrElse(0)
      )
    }.onComplete {
      case Success(_) =>
        Future {
          controller.getPlayers
        }.onComplete {
          case Success(players) => Swing.onEDT { updateLabels(players) }
          case Failure(exception) => Swing.onEDT { showPopup(s"Error fetching players: ${exception.getMessage}") }
        }
      case Failure(exception) =>
        Swing.onEDT {
          showPopup(s"Error placing bet: ${exception.getMessage}")
        }
    }
  }

  centerOnScreen()
  visible = true
}