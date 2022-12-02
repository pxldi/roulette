package Roulette.aview

import Roulette.controller.{Controller, State}
import Roulette.model.*
import Roulette.util.Observer

import scala.swing.*
import scala.swing.Action.NoAction.title
import scala.swing.event.*
import scala.collection.immutable.VectorBuilder

class GUI(controller: Controller) extends Observer { //extends Frame with Observer
  val vc = VectorBuilder[Bet]
  controller.add(this)
  controller.setupPlayers()

  def update: Unit = {
    /*if (controller.bankmoney <= 0) {
      result.text_=("^+.+^ GAME OVER ^+.+^")
      repaint()
      Thread.sleep(5000)
      System.exit(0)
    }
    statusline.text = (">.> " + controller.gameStatus.toString + " <.<")
    nameLine.text_=(controller.name)
    moneyLine.text_=(controller.bankmoney.toString)
    repaint() */
  }

  new Frame {
    title = "Roulette"

    val statusline = new Label(">.> Status <.<")
    val playerCountLine = new TextField("Anzahl Spieler", 10)
    val nameLine = new TextField("Name", 10)
    val startMoneyLine = new TextField("1000", 4)
    val einsatzLine = new TextField("100", 4)
    var result = new Label("")
    var oldMoney = 0
    var newMoney = 0

    def frame = new MainFrame {

      menuBar = new MenuBar {
        contents += new Menu("File") {
          contents += new MenuItem(Action("Quit") {
            System.exit(-1)
          })
          contents += new MenuItem(Action("Create new Player") {
            //controller.createNewPlayer(nameLine.text, bet, moneyLine.text.toInt)
            //controller.set(controller.bankmoney)
            //update
          })
        }
        contents += new Menu("Options") {
          contents += new MenuItem(Action("read") {
            //val js = new FileIO
            //var player = new Player("Jan", "Red", 100)
            //player = js.load
            //controller.createNewPlayer(player.toString, player.playerBet, player.getbankmoney)
            //update
          })
          contents += new MenuItem(Action("save") {
            //val js = new FileIO
            //var player = new Player(controller.name, controller.bet, controller.bankmoney)
            //js.save(player)
          })
        }
      }


      contents = new BoxPanel(Orientation.Vertical) {
        contents += new Label("\n")
        contents += new Label("\n")
        contents += new BoxPanel(Orientation.Horizontal) {
          contents += statusline
          border = Swing.TitledBorder(Swing.EtchedBorder(Swing.Raised), "Status")
          border = Swing.EmptyBorder(10, 10, 10, 10)
        }
        contents += new Label("\n")
        contents += new Label("\n")

        contents += new BoxPanel(Orientation.Horizontal) {
          contents += result
        }
        var actualPlayer = 0
        contents += new BoxPanel(Orientation.Horizontal) {

          contents += Button("Player1") {
            statusline.text = ("Player1 available money: $" + controller.players(0).getAvailableMoney())
            actualPlayer = 0
          }

          contents += Button("Player2") {
            statusline.text = ("Player2 available money: $" + controller.players(1).getAvailableMoney())
            actualPlayer = 1
          }
          contents += Button("Player3") {
            statusline.text = ("Player3 available money: $" + controller.players(2).getAvailableMoney())
            actualPlayer = 2
          }

          contents += Button("Player4") {
            statusline.text = ("Player4 available money: $" + controller.players(3).getAvailableMoney())
            actualPlayer = 3
          }
          contents += Button("Player5") {
            statusline.text = ("Player5 available money: $" + controller.players(4).getAvailableMoney())
            actualPlayer = 4
          }
          border = Swing.TitledBorder(Swing.EtchedBorder(Swing.Raised), "Choose Player")
        }
        var actualBet = 0
        val bet = new Bet
        contents += new BoxPanel(Orientation.Horizontal) {
          contents += new Label("\n")
          contents += einsatzLine
          border = Swing.TitledBorder(Swing.EtchedBorder(Swing.Lowered), "Bet")
        }
        contents += new BoxPanel(Orientation.Horizontal) {
          contents += Button("Safe Bet Money") {
            actualBet = einsatzLine.text.toInt
            statusline.text = ("Bet: " + einsatzLine.text)
          }
        }
        contents += new Label("\n")

        contents += new BoxPanel(Orientation.Horizontal) {
          border = Swing.TitledBorder(Swing.EtchedBorder(Swing.Raised), "Bet options")

          contents += Button("Even") {
            val randomNumber = controller.generateRandomNumber()
            controller.changeState(State.BET)
            vc.clear()

            bet.withBetType("o").withRandomNumber(randomNumber).withPlayerIndex(actualPlayer).withBetAmount(actualBet).withOddOrEven("e")
            vc.addOne(bet)
            controller.changeState(State.RESULT)
            controller.updatePlayer(actualPlayer, bet.bet_amount, false)

            val bets = controller.calculateBets(vc.result())
            for (s <- bets) {
              statusline.text = ("The roulette number is: " + randomNumber + " " + s)
            }
          }

          contents += Button("Odd") {
            val randomNumber = controller.generateRandomNumber()
            controller.changeState(State.BET)
            vc.clear()

            bet.withBetType("o").withRandomNumber(randomNumber).withPlayerIndex(actualPlayer).withBetAmount(actualBet).withOddOrEven("o")
            vc.addOne(bet)
            controller.changeState(State.RESULT)
            controller.updatePlayer(actualPlayer, bet.bet_amount, false)

            val bets = controller.calculateBets(vc.result())
            for (s <- bets) {
              statusline.text = ("The roulette number is: " + randomNumber + " " + s)
            }
          }
          contents += Button("Black") {
            val randomNumber = controller.generateRandomNumber()
            controller.changeState(State.BET)
            vc.clear()

            bet.withBetType("c").withRandomNumber(randomNumber).withPlayerIndex(actualPlayer).withBetAmount(actualBet).withColor("b")
            vc.addOne(bet)
            controller.changeState(State.RESULT)
            controller.updatePlayer(actualPlayer, bet.bet_amount, false)

            val bets = controller.calculateBets(vc.result())
            for (s <- bets) {
              statusline.text = ("The roulette number is: " + randomNumber + " " + s)
            }
          }
          contents += Button("Red") {
            val randomNumber = controller.generateRandomNumber()
            controller.changeState(State.BET)
            vc.clear()

            bet.withBetType("c").withRandomNumber(randomNumber).withPlayerIndex(actualPlayer).withBetAmount(actualBet).withColor("r")
            vc.addOne(bet)
            controller.changeState(State.RESULT)
            controller.updatePlayer(actualPlayer, bet.bet_amount, false)

            val bets = controller.calculateBets(vc.result())
            for (s <- bets) {
              statusline.text = ("The roulette number is: " + randomNumber + " " + s)
            }
          }
        }

        contents += new Label("\n")

        contents += new BoxPanel(Orientation.Horizontal) {
          contents += Button("Undo") {
            //controller.undo
          }
          contents += Button("Redo") {
            //controller.redo
          }
          border = Swing.TitledBorder(Swing.EtchedBorder(Swing.Lowered), "Undo & Redo")
        }
        border = Swing.EmptyBorder(20, 20, 20, 20)
      }
    }

    frame.visible = true
  }

}