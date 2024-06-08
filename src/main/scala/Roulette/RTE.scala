package Roulette

import Roulette.userInterface.guiComponent.guiBaseImpl.GUI
import Roulette.userInterface.tuiComponent.tuiBaseImpl.TUI
import Roulette.controller.controllerComponent.ControllerInterface
import Roulette.controller.controllerComponent.controllerBaseImpl.Controller
import Roulette.fileIO.FileIOInterface
import Roulette.fileIO.xmlImpl.FileIO
import Roulette.core.{Bet, Player}

import scala.concurrent.ExecutionContext.Implicits.global
import reactivemongo.api.{AsyncDriver, MongoConnection}
import reactivemongo.api.bson.{BSONDocument, Macros}
import reactivemongo.api.bson.collection.BSONCollection
import Roulette.db.dao.{MongoDBBetDAO, MongoDBPlayerDAO}

import scala.util.{Failure, Success}

@main
def main(): Unit = {
  val fIO = new FileIO
  given FileIOInterface = fIO

  // MongoDB setup using ReactiveMongo
  val mongoUri = "mongodb://localhost:27017"
  val driver = AsyncDriver()
  val futureParsedUri = MongoConnection.fromString(mongoUri)

  // Database and Collection names
  val dbName = "roulette"
  val playersCollName = "players"
  val betsCollName = "bets"

  // Initialize MongoDB DAOs
  futureParsedUri.flatMap { parsedUri =>
    driver.connect(parsedUri)
  }.onComplete {
    case Success(conn) =>
      val playersDao = new MongoDBPlayerDAO(dbName, playersCollName)(global, conn)
      val betDao = new MongoDBBetDAO(dbName, betsCollName)(global, conn)

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

    case Failure(exception) =>
      println(s"Failed to connect to MongoDB: ${exception.getMessage}")
  }
}


//sbt 'set fork in run := true' run
