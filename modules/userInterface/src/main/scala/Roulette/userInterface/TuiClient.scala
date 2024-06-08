import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods.POST
import akka.http.scaladsl.model.{FormData, HttpCharsets, HttpMethod, HttpMethods, HttpRequest, HttpResponse, Uri}
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

  //val apiBaseUrl: String = "http://localhost:8080/roulette" //use without Docker
  val apiBaseUrl: String = "http://roulette-backend:8080/roulette"

  def main(args: Array[String]): Unit = {
    printGameTitle()
    printInstructions()

    var exit = false
    while (!exit) {
      val input = Option(scala.io.StdIn.readLine(">>> ")).getOrElse("q")  // Default to "q" if null
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
    case "d" => sendHttpRequest("/getResult", HttpMethods.GET)
    case "u" => sendHttpRequest("/undo", HttpMethods.POST)
    case "r" => sendHttpRequest("/redo", HttpMethods.POST)
    case "s" => sendHttpRequest("/save", HttpMethods.POST)
    case "l" => sendHttpRequest("/load", HttpMethods.POST)
    case betCommand if betCommand.startsWith("bet") =>
      println(s"Processing bet command: $betCommand")
      val betDetails = betCommand.drop(4) // Entfernt "bet" vom Anfang des Strings
      sendBetRequest(betDetails)
    case _ => println("Unknown command. Please try again.")
  }

  def sendHttpRequest(apiPath: String, method: HttpMethod): Unit = {
    val uri = s"$apiBaseUrl$apiPath"
    println(s"Sending HTTP request to $uri with method $method")
    val request = HttpRequest(method = method, uri = uri)
    val responseFuture: Future[HttpResponse] = Http().singleRequest(request)

    responseFuture.flatMap(response => Unmarshal(response.entity).to[String])
      .onComplete {
        case Success(content) => println(s"Response from server: $content")
        case Failure(exception) => println(s"An error occurred: $exception")
      }
  }

  def sendBetRequest(input: String): Unit = {
    val parts = input.split(" ").toList
    parts match {
      case p :: t :: v :: a :: Nil =>
        try {
          val playerIndex = p.toInt - 1
          val betAmount = a.toInt
          val value = if (t == "n") Some(v.toInt) else None
          val oddOrEven = if (t == "e" || t == "o") Some(t) else None
          val color = if (t == "c") Some(v) else None
          println(s"Parsed bet request: playerIndex=$playerIndex, betType=$t, value=$value, oddOrEven=$oddOrEven, color=$color, betAmount=$betAmount")
          sendHttpRequest(playerIndex, t, value, oddOrEven, color, betAmount)
        } catch {
          case _: NumberFormatException => println("Please correct your input!")
        }
      case _ => println("Invalid input format.")
    }
  }

  def sendHttpRequest(playerIndex: Int, betType: String, value: Option[Int], oddOrEven: Option[String], color: Option[String], betAmount: Int)(implicit system: ActorSystem): Unit = {
    import system.dispatcher // Import to handle futures

    // Prepare form data
    val formData = FormData(
      "playerIndex" -> playerIndex.toString,
      "betType" -> betType,
      "value" -> value.map(_.toString).getOrElse(""),
      "oddOrEven" -> oddOrEven.getOrElse(""),
      "color" -> color.getOrElse(""),
      "betAmount" -> betAmount.toString
    )

    // Econvert to a UTF-8 charset
    val requestEntity = formData.toEntity(HttpCharsets.`UTF-8`)

    val request = HttpRequest(
      method = POST,
      uri = s"$apiBaseUrl/createAndAddBet",
      entity = requestEntity
    )


    // Send the request using the Akka HTTP client
    Http().singleRequest(request).onComplete {
      case Success(response) =>
        Unmarshal(response.entity).to[String].onComplete {
          case Success(content) => println(s"Response from server: $content")
          case Failure(exception) => println(s"Failed to parse response: $exception")
        }
      case Failure(exception) =>
        println(s"Request failed: $exception")
    }
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

// sbt "runMain Roulette.userInterface.TuiClient"
// bet 1 e e 13

