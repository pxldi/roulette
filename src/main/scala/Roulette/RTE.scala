package Roulette

import Roulette.aview.GUI
import Roulette.aview.tuiComponent.tuiBaseImpl.TUI
import Roulette.controller.controllerComponent.controllerBaseImpl.Controller

@main
def main(): Unit =

  val controller = new Controller
  controller.generateRandomNumber()
  controller.setupPlayers()
  val tui = TUI(controller)
  val gui = GUI(controller)

  val cliThread = new Thread(() =>
    tui.start()
    System.exit(0)
  )
  cliThread.setDaemon(true)
  cliThread.start()
