package Roulette.controller

import Roulette.controller.controllerComponent.ControllerInterface
import Roulette.controller.controllerComponent.controllerBaseImpl.Controller
import Roulette.fileIO.FileIOInterface
import Roulette.fileIO.xmlImpl.FileIO
import Roulette.core.{Bet, Player, PlayerUpdate}
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.model.*
import io.circe.{Json as CirceJson, *}
import io.circe.generic.semiauto.*
import io.circe.generic.auto.*
import io.circe.syntax.*
import io.circe.parser.*
import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites
import play.api.libs.json.Format.GenericFormat
import reactivemongo.api.AsyncDriver

import scala.concurrent.ExecutionContext.Implicits.global
import reactivemongo.api.{AsyncDriver, MongoConnection}
import reactivemongo.api.bson.{BSONDocument, Macros}
import reactivemongo.api.bson.collection.BSONCollection
import Roulette.db.dao.{MongoDBBetDAO, MongoDBPlayerDAO}

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}
import scala.io.StdIn

case class PlayerInfo(index: Int, money: Int)
case class ApiResponse(status: String, message: String)

object ControllerApi {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "RouletteHttpServer")
    implicit val executionContext: ExecutionContextExecutor = system.executionContext

    val fIO = new FileIO
    given FileIOInterface = fIO

    // MongoDB
    val mongoUri = "mongodb://localhost:27017"
    val driver = AsyncDriver()
    val futureParsedUri = MongoConnection.fromString(mongoUri)
    val dbName = "roulette"
    val playersCollName = "players"
    val betsCollName = "bets"

    val controllerFuture: Future[ControllerInterface] = futureParsedUri.flatMap { parsedUri =>
      driver.connect(parsedUri).map { conn =>
        val playersDao = new MongoDBPlayerDAO(dbName, playersCollName)(global, conn)
        val betDao = new MongoDBBetDAO(dbName, betsCollName)(global, conn)
        new Controller(using fIO, playersDao, betDao)
      }
    }
    
    // 

    val route = concat(
      path("roulette" / "undo") {
        post {
          onComplete(controllerFuture) {
            case Success(controller) =>
              controller.undo()
              complete(HttpEntity(ContentTypes.`application/json`, ApiResponse("success", "Undo successful").asJson.noSpaces))
            case Failure(exception) =>
              complete(HttpEntity(ContentTypes.`application/json`, ApiResponse("error", s"Failed to initialize controller: ${exception.getMessage}").asJson.noSpaces))
          }
        }
      },
      path("roulette" / "redo") {
        post {
          onComplete(controllerFuture) {
            case Success(controller) =>
              controller.redo()
              complete(HttpEntity(ContentTypes.`application/json`, ApiResponse("success", "Redo successful").asJson.noSpaces))
            case Failure(exception) =>
              complete(HttpEntity(ContentTypes.`application/json`, ApiResponse("error", s"Failed to initialize controller: ${exception.getMessage}").asJson.noSpaces))
          }
        }
      },
      path("roulette" / "save") {
        post {
          onComplete(controllerFuture) {
            case Success(controller) =>
              controller.save()
              complete(StatusCodes.OK, "Game saved")
            case Failure(exception) =>
              complete(StatusCodes.InternalServerError, s"Failed to initialize controller: ${exception.getMessage}")
          }
        }
      },
      path("roulette" / "load") {
        post {
          onComplete(controllerFuture) {
            case Success(controller) =>
              controller.load()
              complete(StatusCodes.OK, "Game loaded")
            case Failure(exception) =>
              complete(StatusCodes.InternalServerError, s"Failed to initialize controller: ${exception.getMessage}")
          }
        }
      },
      path("roulette" / "createAndAddBet") {
        post {
          formFields(
            "playerIndex".as[Int],
            "betType",
            "value".as[Int].optional,
            "oddOrEven".optional,
            "color".optional,
            "betAmount".as[Int]
          ) { (playerIndex, betType, value, oddOrEven, color, betAmount) =>
            onComplete(controllerFuture) {
              case Success(controller) =>
                // Debugging-Statements zur Überprüfung der empfangenen Werte
                println(s"Received bet with: playerIndex=$playerIndex, betType=$betType, value=$value, oddOrEven=$oddOrEven, color=$color, betAmount=$betAmount")

                controller.setupPlayers()

                // Versuch, die Wette zu erstellen
                try {
                  val betResultFuture = controller.createAndAddBet(playerIndex, betType, value, oddOrEven, color, betAmount)
                  onComplete(betResultFuture) {
                    case Success(result) =>
                      if (result) {
                        println("Bet creation successful.")
                        complete(StatusCodes.OK, "Bet created successfully")
                      } else {
                        println("Bet creation failed.")
                        complete(StatusCodes.BadRequest, "Failed to create bet")
                      }
                    case Failure(exception) =>
                      println(s"Exception during bet creation: ${exception.getMessage}")
                      complete(StatusCodes.InternalServerError, s"Failed due to internal server error: ${exception.getMessage}")
                  }
                } catch {
                  case e: Exception =>
                    println(s"Exception during bet creation: ${e.getMessage}")
                    complete(StatusCodes.InternalServerError, s"Failed due to internal server error: ${e.getMessage}")
                }
              case Failure(exception) =>
                complete(StatusCodes.InternalServerError, s"Failed to initialize controller: ${exception.getMessage}")
            }
          }
        }
      },
      path("roulette" / "getResult") {
        get {
          onComplete(controllerFuture) {
            case Success(controller) =>
              // Rufen Sie die Funktion des Controllers auf und erfassen Sie das Ergebnis
              val result = controller.calculateBets()
              println(s"Calculated results: $result")

              // Senden Sie das Ergebnis als HTTP-Response zurück
              complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, result.toString))
            case Failure(exception) =>
              complete(StatusCodes.InternalServerError, s"Failed to initialize controller: ${exception.getMessage}")
          }
        }
      }
    )

    val bindingFuture = Http().newServerAt("localhost", 8080).bind(route) //use without docker
    //val bindingFuture = Http().newServerAt("0.0.0.0", 8080).bind(route)
    //println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    println(s"Server online at http://0.0.0.0:8080/\nPress RETURN to stop...")
    StdIn.readLine() // Lässt den Server laufen, bis der Benutzer Return drückt
    bindingFuture
      .flatMap(_.unbind()) // Löst die Bindung vom Port
      .onComplete(_ => system.terminate()) // Beendet das System
  }
}
// wsl
// 1. Ordner mit build.sbt
// sbt "runMain Roulette.controller.controllerApi"

//second terminal
//wsl
//curl -X POST -i http://localhost:8080/roulette/setupPlayers
