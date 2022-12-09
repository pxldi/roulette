package Roulette.aview

import Roulette.controller.controllerComponent.State
import Roulette.controller.controllerComponent.controllerBaseImpl.Controller
import Roulette.util.Event
import Roulette.model.*
import Roulette.util.Observer

import java.awt.{Dimension, Rectangle}
import scala.swing.*
import scala.swing.Action.NoAction.title
import scala.swing.event.*
import scala.collection.immutable.VectorBuilder

class GUI(controller: Controller) extends Observer { // extends Frame with Observer

  private val state_label = new Label("Welcome to Roulette!")
  private val player_one_money = new Label("P1: " + controller.players(0).getAvailableMoney + "$")
  private val player_two_money = new Label("P2: " + controller.players(1).getAvailableMoney + "$")
  private val result = new Label("Bet Result")
  private val bet_amount_textfield = new TextField("0", 4)
  private val bet_number_textfield = new TextField("0", 4)

  private var selected_player = 0
  private var bet_type = "n"
  private var bet_value = "0"
  private var bet_amount = 0

  bet_number_textfield.editable = false
  controller.add(this)

  private def updateLabels(): Unit =
    player_one_money.text = "P1: " + controller.players(0).getAvailableMoney + "$"
    player_two_money.text = "P2: " + controller.players(1).getAvailableMoney + "$"

  private def showPopup(message: String): Unit =
    val dialog = new Dialog()
    dialog.contents = new Label(message)
    dialog.bounds = Rectangle(200, 100)
    dialog.centerOnScreen()
    dialog.visible = true

  override def update(e: Event): Unit = {
    e match
      case Event.DRAW => showPopup("The game ended in a draw!"); updateLabels()
      case Event.P1WIN => showPopup("The game ended. Player 1 won!"); updateLabels()
      case Event.P2WIN => showPopup("The game ended. Player 2 won!"); updateLabels()
      case Event.UPDATE => updateLabels()
      case Event.QUIT => System.exit(0)
  }

  new Frame {

    private def frame = new MainFrame {

      // Menu Bar
      menuBar = new MenuBar {
        contents += new Menu("File") {
          contents += new MenuItem(Action("Save") {})
          contents += new MenuItem(Action("Load") {})
          contents += new MenuItem(Action("Quit") {
            controller.quit()
          })
        }
      }

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
            selected_player = 0
          }

          contents += Swing.HStrut(10)

          contents += Button("Player 2") {
            selected_player = 1
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
            result.text = controller.calculateBets().mkString("/n")
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
    }
    frame.centerOnScreen()
    frame.visible = true
  }

  private def createBet(): Unit =
    val bet = new Bet
    bet_amount = bet_amount_textfield.text.toInt
    bet_type match
      case "n" =>
        bet
          .withPlayerIndex(selected_player)
          .withBetType(bet_type)
          .withBetNumber(bet_number_textfield.text.toInt)
          .withBetAmount(bet_amount)
      case "e" =>
        bet
          .withPlayerIndex(selected_player)
          .withBetType(bet_type)
          .withOddOrEven(bet_value)
          .withBetAmount(bet_amount)
      case "c" =>
        bet
          .withPlayerIndex(selected_player)
          .withBetType(bet_type)
          .withColor(bet_value)
          .withBetAmount(bet_amount)
      case _ =>
        println("XD")
    controller.addBet(bet)
}
