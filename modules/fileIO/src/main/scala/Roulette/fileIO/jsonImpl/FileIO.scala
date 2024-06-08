package Roulette.fileIO.jsonImpl

import Roulette.fileIO.FileIOInterface
import Roulette.core.{Bet, Player}
import play.api.libs.json._
import play.api.libs.functional.syntax._
import scala.io.Source
import java.io._
import java.nio.file.{Files, Paths}

class FileIO extends FileIOInterface {

  implicit val playerWrites: Writes[Player] = (
    (JsPath \ "player_index").write[Int] and
      (JsPath \ "available_money").write[Int]
    )(unlift(Player.unapply))

  implicit val playerReads: Reads[Player] = (
    (JsPath \ "player_index").read[Int] and
      (JsPath \ "available_money").read[Int]
    )(Player.apply _)

  implicit val betWrites: Writes[Bet] = (
    (JsPath \ "bet_type").write[Option[String]] and
      (JsPath \ "player_index").write[Option[Int]] and
      (JsPath \ "bet_number").write[Option[Int]] and
      (JsPath \ "bet_odd_or_even").write[Option[String]] and
      (JsPath \ "bet_color").write[Option[String]] and
      (JsPath \ "bet_amount").write[Option[Int]] and
      (JsPath \ "random_number").write[Option[Int]]
    )(b => (
    b.bet_type,
    b.player_index,
    b.bet_number,
    b.bet_odd_or_even,
    b.bet_color,
    b.bet_amount,
    b.random_number
  ))

  implicit val betReads: Reads[Bet] = (
    (JsPath \ "bet_type").readNullable[String] and
      (JsPath \ "player_index").readNullable[Int] and
      (JsPath \ "bet_number").readNullable[Int] and
      (JsPath \ "bet_odd_or_even").readNullable[String] and
      (JsPath \ "bet_color").readNullable[String] and
      (JsPath \ "bet_amount").readNullable[Int] and
      (JsPath \ "random_number").readNullable[Int]
    )(Bet.apply _)

  override def save(vector_player: Vector[Player], vector_bet: Vector[Bet]): Unit = {
    val path = Paths.get("saves/")
    if (!Files.exists(path))
      Files.createDirectory(path)
    val pw = new PrintWriter(new File(path.toString, "savefile.json"))
    pw.write(Json.prettyPrint(vectorsToJson(vector_player, vector_bet)))
    pw.close()
  }

  private def vectorsToJson(vector_player: Vector[Player], vector_bet: Vector[Bet]): JsObject = Json.obj(
    "players" -> Json.toJson(vector_player),
    "bets" -> Json.toJson(vector_bet)
  )

  override def load(): (Vector[Player], Vector[Bet]) = {
    val source: String = Source.fromFile("saves/savefile.json").getLines.mkString
    val json: JsValue = Json.parse(source)

    val players: Vector[Player] = (json \ "players").as[Vector[Player]]
    val bets: Vector[Bet] = (json \ "bets").as[Vector[Bet]]

    (players, bets)
  }
}
