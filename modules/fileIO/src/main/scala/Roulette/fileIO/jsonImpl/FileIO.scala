package Roulette.fileIO.jsonImpl

import Roulette.fileIO.FileIOInterface
import Roulette.core.{Bet, Player}
import play.api.libs.json._
import play.api.libs.functional.syntax._
import scala.collection.immutable.VectorBuilder
import scala.io.Source
import java.io._
import java.nio.file.{Files, Paths}
import java.util.UUID

class FileIO extends FileIOInterface {

  implicit val playerWrites: Writes[Player] = (
    (JsPath \ "id").write[UUID] and
    (JsPath \ "available_money").write[Int]
  )(player => (
      player.id,
      player.available_money
    )
  )

  implicit val playerReads: Reads[Player] = (
    (JsPath \ "id").read[UUID] and
      (JsPath \ "available_money").read[Int]
    )(Player.apply _)

  implicit val betWrites: Writes[Bet] = (
    (JsPath \ "id").write[Option[UUID]] and
    (JsPath \ "bet_type").write[Option[String]] and
    (JsPath \ "player_id").write[Option[UUID]] and
    (JsPath \ "bet_number").write[Option[Int]] and
    (JsPath \ "bet_odd_or_even").write[Option[String]] and
    (JsPath \ "bet_color").write[Option[String]] and
    (JsPath \ "bet_amount").write[Option[Int]] and
    (JsPath \ "random_number").write[Option[Int]]
  )(bet => (
      bet.id,
      bet.bet_type,
      bet.player_id,
      bet.bet_number,
      bet.bet_odd_or_even,
      bet.bet_color,
      bet.bet_amount,
      bet.random_number
    )
  )

  implicit val betReads: Reads[Bet] = (
    (JsPath \ "id").readNullable[UUID] and
    (JsPath \ "bet_type").readNullable[String] and
    (JsPath \ "player_id").readNullable[UUID] and
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