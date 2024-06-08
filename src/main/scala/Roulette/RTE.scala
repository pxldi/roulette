package Roulette

import Roulette.userInterface.guiComponent.guiBaseImpl.GUI
import Roulette.userInterface.tuiComponent.tuiBaseImpl.TUI
import Roulette.controller.controllerComponent.ControllerInterface
import Roulette.controller.controllerComponent.controllerBaseImpl.Controller
import Roulette.fileIO.FileIOInterface
import Roulette.fileIO.xmlImpl.FileIO
import Roulette.core.{Bet, Player}
import Roulette.db.dao.{PlayerDAO, BetDAO, MongoDBPlayerDAO, MongoDBBetDAO, SlickPlayerDAO, SlickBetDAO}

import scala.concurrent.ExecutionContext.Implicits.global
import reactivemongo.api.{AsyncDriver, MongoConnection}
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.Future
import scala.util.{Failure, Success}
import com.typesafe.config.ConfigFactory

@main
def main(): Unit = {
  val fIO = new FileIO
  given FileIOInterface = fIO

  val config = ConfigFactory.load()
  val useSlick = config.getBoolean("db.useSlick")

  val controllerFuture = if (useSlick) {
    // Initialize Slick DAOs
    val dbConfig = Database.forConfig("db.slick.dbs.default.db")
    val profile: JdbcProfile = slick.jdbc.PostgresProfile
    val playersDao = new SlickPlayerDAO(dbConfig)
    val betDao = new SlickBetDAO(dbConfig)
    Future.successful(new Controller(using fIO, playersDao, betDao))
  } else {
    // Initialize MongoDB DAOs
    val mongoUri = "mongodb://localhost:27017"
    val driver = AsyncDriver()
    val futureParsedUri = MongoConnection.fromString(mongoUri)

    futureParsedUri.flatMap { parsedUri =>
      driver.connect(parsedUri).map { conn =>
        val playersDao = new MongoDBPlayerDAO("roulette", "players")(global, conn)
        val betDao = new MongoDBBetDAO("roulette", "bets")(global, conn)
        new Controller(using fIO, playersDao, betDao)
      }
    }
  }

  controllerFuture.onComplete {
    case Success(controller) =>
      given ControllerInterface = controller

      controller.generateRandomNumber()
      controller.setupPlayers()

      val tui = new TUI()
      val gui = new GUI()

      val tuiThread = new Thread(() => {
        tui.start()
        System.exit(0)
      })
      tuiThread.setDaemon(true)
      tuiThread.start()

      gui.visible = true

    case Failure(exception) =>
      println(s"Failed to initialize controller: ${exception.getMessage}")
  }

  // Keep the main thread alive
  while (true) {
    Thread.sleep(1000)
  }
}
