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

import akka.kafka.{ConsumerSettings, Subscriptions}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import akka.kafka.scaladsl.Consumer


object TuiClient {
  implicit val system: ActorSystem = ActorSystem()
  import system.dispatcher // für Futures
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: scala.concurrent.ExecutionContext = system.dispatcher

  //val apiBaseUrl: String = "http://localhost:8085/roulette" //use without Docker
  val apiBaseUrl: String = "http://roulette-backend:8080/roulette" //use with Docker

  val consumerSettings = ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
    .withBootstrapServers("kafka:9092") // Kafka server address for Docker: kafka:9092 , for local: localhost:9092
    .withGroupId("roulette-group")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  val topics = Subscriptions.topics("roulette-results")

  def consumeResults(): Unit = {
    val consumerSource = Consumer.plainSource(consumerSettings, topics)

    consumerSource
      .filter(msg => relevant(msg.value()))
      .mapAsync(1) { msg =>
        Future {
            println(s"Relevant result received: ${msg.value()}")
            // Logik für Frontend verarbeitung...
            msg.value()
        }
      }.runWith(Sink.ignore)
  }

  def relevant(message: String): Boolean = {
    // Beispiellogik: Prüfen, ob die Nachricht ein bestimmtes Schlüsselwort enthält
    message.contains("Player")
  }


  def main(args: Array[String]): Unit = {
    printGameTitle()
    printInstructions()

    // Start consuming results from Kafka
    consumeResults()

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
      val betDetails = betCommand.drop(4) // Remove "bet" from the beginning of the string
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
        case Success(content) =>
          val results = content.stripPrefix("Vector(").stripSuffix(")").split(", ").toVector
          results.foreach(println)
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
          val oddOrEven = if (t == "e") Some(v) else None
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

// sbt "runMain TuiClient"
// bet 1 e e 13

