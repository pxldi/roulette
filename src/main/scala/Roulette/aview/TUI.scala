package Roulette.aview

import Roulette.model.Player
import Roulette.util.Observer
import Roulette.util.Event
import Roulette.model.Bet
import Roulette.controller.Controller
import Roulette.controller.State

import scala.io.StdIn.readLine
import scala.collection.immutable.VectorBuilder

case class TUI(controller: Controller) extends Observer:

  override def update(idx: Int, e: Event): Unit = {
    e match
        case Event.QUIT => println("Quitting")
        case Event.PLAY =>  println("Player" + (idx+1))
                            println("Your actual Money is: " + controller.players(idx).getAvailableMoney())
                            println("Game State: " + controller.getState())
  }

  val vc = VectorBuilder[Bet]
  controller.add(this)
  controller.setupPlayers()

  val cliThread = new Thread(() =>
    inputLoop()
      System.exit(0)
  )
  cliThread.setDaemon(true)
  cliThread.start()

  def inputLoop(): Unit = {
    val randomNumber = controller.generateRandomNumber()
    controller.changeState(State.BET)
    vc.clear()
    println("\n" + controller.printState())

    while(controller.state == State.BET) {
      for (player_index <- 0 until controller.getPlayerCount()) {
        print("\nTurn of player " + (player_index + 1) + "\n" + "Available money: $" + controller.players(player_index).getAvailableMoney() + "\n")

        val bet = new Bet
        val bet_type = readLine("\nDo you want to place a bet on a number (n), on odd or even (o) or on a color (c)? If one of the players types (d), the betting phase will stop.>>>")

        bet_type match
          case "n" =>
            val bet_number = readLine("On which number do you want to place your bet? (0-36) >>>").toInt
            val bet_amount = readLine("How much money do you want to bet? >>>$").toInt
            bet.withBetType(bet_type).withRandomNumber(randomNumber).withPlayerIndex(player_index).withBetNumber(bet_number).withBetAmount(bet_amount)
            vc.addOne(bet)
            print("<<<Your bet was placed!>>>\n")
          case "o" =>
            val bet_odd_or_even = readLine("Do you want to bet on odd (o) or even (e)? >>>")
            val bet_amount = readLine("How much money do you want to bet? >>>$").toInt
            bet.withBetType(bet_type).withRandomNumber(randomNumber).withPlayerIndex(player_index).withOddOrEven(bet_odd_or_even).withBetAmount(bet_amount)
            vc.addOne(bet)
            print("<<<Your bet was placed!>>>\n")
          case "c" =>
            val bet_color = readLine("Do you want to bet on red (r) or black (b)? >>>")
            val bet_amount = readLine("How much money do you want to bet? >>>$").toInt
            bet.withBetType(bet_type).withRandomNumber(randomNumber).withPlayerIndex(player_index).withColor(bet_color).withBetAmount(bet_amount)
            vc.addOne(bet)
            print("<<<Your bet was placed!>>>\n")
          case "d" =>
            controller.changeState(State.RESULT)
          //case "z" => controller.redo
          //case "y" => controller.undo
          
          controller.updatePlayer(player_index, bet.bet_amount, false)
          update(player_index, Event.PLAY)
      }
    }
    println(controller.printState())
    print("The roulette number is: " + randomNumber)
    val bets = controller.calculateBets(vc.result())
    for (s <- bets) {
      println(s)
    }
    inputLoop()
  }
