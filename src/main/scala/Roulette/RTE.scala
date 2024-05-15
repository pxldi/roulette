package Roulette

import Roulette.aview.guiComponent.guiBaseImpl.GUI
import Roulette.aview.tuiComponent.tuiBaseImpl.TUI
import Roulette.controller.controllerComponent.ControllerInterface
import Roulette.controller.controllerComponent.controllerBaseImpl.Controller
import Roulette.fileIO.FileIOInterface
import Roulette.fileIO.xmlImpl.FileIO
import Roulette.db.dao.{PlayerDAO, BetDAO}
import scala.concurrent.ExecutionContext.Implicits.global
import Roulette.db.dao.{SlickPlayerDAO, SlickBetDAO}
import slick.jdbc.PostgresProfile.api._

@main
def main(): Unit = {
  val fIO = new FileIO
  given FileIOInterface = fIO
  val db = Database.forConfig("slick.dbs.default.db")
  val playersDao = new SlickPlayerDAO(db)
  val betDao = new SlickBetDAO(db)
  val controller = new Controller(using fIO, playersDao, betDao)
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
}
