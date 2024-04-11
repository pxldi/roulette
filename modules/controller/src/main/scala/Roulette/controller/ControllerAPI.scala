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

object controllerApi {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "RouletteHttpServer")
    implicit val executionContext: ExecutionContextExecutor = system.executionContext

    val fIO = new FileIO
    given FileIOInterface = fIO
    val controller = new Controller

    val route = concat(
      // Vorhandene Routen beibehalten
      path("roulette" / "addBet") {
        post {
          entity(as[String]) { betJson =>
            decode[Bet](betJson) match {
              case Right(bet) =>
                if (controller.addBet(bet)) {
                  complete(HttpEntity(ContentTypes.`application/json`, ApiResponse("success", "Bet added successfully").asJson.noSpaces))
                } else {
                  complete(StatusCodes.BadRequest, HttpEntity(ContentTypes.`application/json`, ApiResponse("error", "Failed to add bet").asJson.noSpaces))
                }
              case Left(error) =>
                complete(StatusCodes.BadRequest, HttpEntity(ContentTypes.`application/json`, ApiResponse("error", "Invalid JSON").asJson.noSpaces))
            }
          }
        }
      },
      path("roulette" / "calculateBets") {
        post {
          val results = controller.calculateBets()
          complete(HttpEntity(ContentTypes.`application/json`, results.asJson.noSpaces))
        }
      },
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
      // path("roulette" / "quit") { ... } // Security
      path("roulette" / "getRandomNumber") {
        get {
          complete(HttpEntity(ContentTypes.`application/json`, Map("randomNumber" -> controller.randomNumber).asJson.noSpaces))
        }
      },
      // Für getPlayers()(0) benötigst du ggf. eine spezielle Logik oder eine Erweiterung deines PlayerInfo-Modells.
      path("roulette" / "getPlayer") {
        get {
          parameter("index".as[Int]) { index =>
            if(index >= 0 && index < controller.getPlayers().size) {
              val player = controller.getPlayers()(index)
              complete(HttpEntity(ContentTypes.`application/json`, PlayerInfo(index, player.getAvailableMoney).asJson.noSpaces))
            } else {
              complete(StatusCodes.BadRequest, "Invalid player index")
            }
          }
        }
      },
      path("roulette" / "bet") {
        post {
          entity(as[String]) { betJson =>
            decode[Bet](betJson) match {
              case Right(bet) =>
                // TODO: bet in controller einbauen. View kein Zugriff auf Model
                // z.B. es zum Zustand hinzufügen oder eine Aktion darauf anwenden
                complete(HttpEntity(ContentTypes.`application/json`, s"Bet received: ${bet.asJson.noSpaces}"))

              case Left(error) =>
                complete(StatusCodes.BadRequest, s"Invalid Bet JSON: $error")
            }
          }
        }
      }
        // TODO: Füge hier weitere Routen für restliche methoden hinzu:
        //  Controller api: addBet(), calculateBets(), undo(), redo(), save() , load() , Quit(), randomNumber(), getPlayers()(0).
        //  Model api: Player -> getAvailableMoney, Bet
      )

    val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
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