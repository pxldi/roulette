package Roulette

import Roulette.aview.guiComponent.guiBaseImpl.GUI
import Roulette.aview.tuiComponent.tuiBaseImpl.TUI
import Roulette.controller.controllerComponent.ControllerInterface
import Roulette.controller.controllerComponent.controllerBaseImpl.Controller
import Roulette.aview.tuiComponent.TUIInterface

@main
def main(): Unit =

  val controller = new Controller
  given ControllerInterface = controller
  controller.generateRandomNumber()
  controller.setupPlayers()
  val tui = TUI()
  val gui = GUI()

  val cliThread = new Thread(() =>
    tui.start()
    System.exit(0)
  )
  cliThread.setDaemon(true)
  cliThread.start()
