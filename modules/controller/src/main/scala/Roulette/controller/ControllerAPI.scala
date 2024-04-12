package Roulette.controller

import Roulette.controller.controllerComponent.controllerBaseImpl.Controller
import Roulette.fileIO.FileIOInterface
import Roulette.fileIO.xmlImpl.FileIO
import Roulette.core.{Bet, Player, PlayerUpdate}

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.model.*

import io.circe.{Json => CirceJson, _}
import io.circe.generic.semiauto._
import io.circe.generic.auto._ //wichtig
import io.circe.syntax._
import io.circe.parser._
import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites
import play.api.libs.json.Format.GenericFormat

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

case class PlayerInfo(index: Int, money: Int)
case class ApiResponse(status: String, message: String)

object ControllerApi {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "RouletteHttpServer")
    implicit val executionContext: ExecutionContextExecutor = system.executionContext

    val fIO = new FileIO
    given FileIOInterface = fIO
    val controller = new Controller

    val route = concat(
      path("roulette" / "undo") {
        post {
          controller.undo()
          complete(HttpEntity(ContentTypes.`application/json`, ApiResponse("success", "Undo successful").asJson.noSpaces))
        }
      },
      path("roulette" / "redo") {
        post {
          controller.redo()
          complete(HttpEntity(ContentTypes.`application/json`, ApiResponse("success", "Redo successful").asJson.noSpaces))
        }
      },
      path("roulette" / "save") {
        post {
          controller.save()
          complete(StatusCodes.OK, "Game saved")
        }
      },
      path("roulette" / "load") {
        post {
          controller.load()
          complete(StatusCodes.OK, "Game loaded")
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
            // Debugging-Statements zur Überprüfung der empfangenen Werte
            println(s"Received bet with: playerIndex=$playerIndex, betType=$betType, value=$value, oddOrEven=$oddOrEven, color=$color, betAmount=$betAmount")

            controller.setupPlayers()

            // Versuch, die Wette zu erstellen
            try {
              val result = controller.postBet(playerIndex, betType, value, oddOrEven, color, betAmount)
              if (result) {
                println("Bet creation successful.")
                complete(StatusCodes.OK, "Bet created successfully")
              } else {
                println("Bet creation failed.")
                complete(StatusCodes.BadRequest, "Failed to create bet")
              }
            } catch {
              case e: Exception =>
                println(s"Exception during bet creation: ${e.getMessage}")
                complete(StatusCodes.InternalServerError, s"Failed due to internal server error: ${e.getMessage}")
            }
          }
        }
      },
      path("roulette" / "getResult") {
        get {
          // Rufen Sie die Funktion des Controllers auf und erfassen Sie das Ergebnis
          val result = controller.getCalculateBets()
          println(s"Calculated results: $result")

          // Senden Sie das Ergebnis als HTTP-Response zurück
          complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, result.toString))
        }
      }
    )

    //val bindingFuture = Http().newServerAt("localhost", 8080).bind(route) //use without docker
    val bindingFuture = Http().newServerAt("0.0.0.0", 8080).bind(route)
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
