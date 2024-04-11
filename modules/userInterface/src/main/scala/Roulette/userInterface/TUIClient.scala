import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethod, HttpMethods, HttpRequest, HttpResponse}
import akka.http.scaladsl.model.ws.{Message, TextMessage, WebSocketRequest}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import akka.stream.{ActorMaterializer, OverflowStrategy}

import scala.concurrent.duration.*
import scala.concurrent.{Await, Future}
import scala.io.StdIn.readLine
import scala.util.{Failure, Success}

object TuiClient {
  implicit val system: ActorSystem = ActorSystem()
  import system.dispatcher // für Futures
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: scala.concurrent.ExecutionContext = system.dispatcher

  val apiBaseUrl: String = "http://localhost:8080/roulette"

  def main(args: Array[String]): Unit = {
    printGameTitle()
    printInstructions()

    var exit = false
    while (!exit) {
      val input = readLine(">>> ")
      input.trim match {
        case "q" =>
          println("Quitting...")
          exit = true
        case command =>
          handleCommand(command)
      }
    }

    system.terminate()
  }

  def handleCommand(command: String): Unit = command match {
    case "d" => sendHttpRequest("/calculateBets", HttpMethods.POST)
    case "u" => sendHttpRequest("/undo", HttpMethods.POST)
    case "r" => sendHttpRequest("/redo", HttpMethods.POST)
    case "s" => sendHttpRequest("/save", HttpMethods.POST)
    case "l" => sendHttpRequest("/load", HttpMethods.POST)
    case betCommand if betCommand.startsWith("bet") =>
      val betDetails = betCommand.drop(4) // Entfernt "bet" vom Anfang des Strings
      sendBetRequest(betDetails)
    case _ => println("Unknown command. Please try again.")
  }

  def sendHttpRequest(apiPath: String, method: HttpMethod): Unit = {
    val request = HttpRequest(method = method, uri = s"$apiBaseUrl$apiPath")
    val responseFuture: Future[HttpResponse] = Http().singleRequest(request)

    responseFuture.flatMap(response => Unmarshal(response.entity).to[String])
      .onComplete {
        case Success(content) => println(content)
        case Failure(exception) => println(s"An error occurred: $exception")
      }
  }

  def sendBetRequest(betDetails: String): Unit = {
    // Logik zum Parsen und Senden der Bet-Anfrage hier implementieren
    println(s"Sending bet: $betDetails")
    // Beispiel für ein POST-Request mit einem JSON-Body:
    // sendHttpRequest("/addBet", HttpMethods.POST, betDetails)
  }

  def printGameTitle(): Unit = {
    println("""
              | _____             _      _   _
              ||  __ \           | |    | | | |
              || |__) |___  _   _| | ___| |_| |_ ___
              ||  _  // _ \| | | | |/ _ \ __| __/ _ \
              || | \ \ (_) | |_| | |  __/ |_| ||  __/
              ||_|  \_\___/ \__,_|_|\___|\__|\__\___|
              |""".stripMargin)
  }

  def printInstructions(): Unit = {
    println("""
              |Instructions: Type...
              |>>> "bet [Player number (1 or 2)] [Bet type (n / e / c)] [Bet value (0 - 36 / e or o / r or b)] [bet amount]" to bet.
              |>>> "u" or "r" to undo or redo respectively.
              |>>> "d" to stop the betting phase and spin the wheel.
              |>>> "q" to quit the game.
              |""".stripMargin)
  }
}
