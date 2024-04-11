package Roulette.fileIO.jsonImpl

import Roulette.fileIO.FileIOInterface
import Roulette.core.{Bet, Player}

import play.api.libs.*
import play.api.libs.json.{JsObject, JsValue, Json}

import scala.collection.immutable.VectorBuilder
import scala.io.Source
import java.io.*
import java.nio.file.{Files, Paths}
import javax.management.MBeanTrustPermission

class FileIO extends FileIOInterface {
  override def save(vector_player: Vector[Player], vector_bet: Vector[Bet]): Unit =
    val path = Paths.get("saves/")
    if (!Files.exists(path))
      Files.createDirectory(path)
    val pw = new PrintWriter(new File(path.toString, "savefile.json"))
    pw.write(Json.prettyPrint(vectorsToJson(vector_player, vector_bet)))
    pw.close()

  private def vectorsToJson(vector_player: Vector[Player], vector_bet: Vector[Bet]): JsObject =
    Json.obj(
      "players" -> Json.toJson(for {
        i <- vector_player.indices
      } yield Json.obj("available_money" -> Json.toJson(vector_player(i).getAvailableMoney))),
      "bets" -> Json.toJson(for {
        i <- vector_bet.indices
      } yield {
        Json.obj(
          "bet_type" -> Json.toJson(vector_bet(i).bet_type),
          "player_index" -> Json.toJson(vector_bet(i).player_index),
          "bet_number" -> Json.toJson(vector_bet(i).bet_number),
          "bet_odd_or_even" -> Json.toJson(vector_bet(i).bet_odd_or_even),
          "bet_color" -> Json.toJson(vector_bet(i).bet_color),
          "bet_amount" -> Json.toJson(vector_bet(i).bet_amount))
      }))
  override def load(): (Vector[Player], Vector[Bet]) =
    val vc_player = VectorBuilder[Player]
    val vc_bets = VectorBuilder[Bet]
    val source: String = Source.fromFile("saves/savefile.json").getLines.mkString
    val json: JsValue = Json.parse(source)
    val money_vec = (json \ "players" \\ "available_money").map(x => x.as[Int])
    vc_player.addOne(Player(money_vec.head))
    vc_player.addOne(Player(money_vec(1)))
    val bet_type_vec = (json \ "bets" \\ "bet_type").map(x => x.as[String])
    val player_index_vec = (json \ "bets" \\ "player_index").map(x => x.as[Int])
    val bet_number_vec = (json \ "bets" \\ "bet_number").map(x => x.as[Int])
    val bet_odd_or_even_vec = (json \ "bets" \\ "bet_odd_or_even").map(x => x.as[String])
    val bet_color_vec = (json \ "bets" \\ "bet_color").map(x => x.as[String])
    val bet_amount_vec = (json \ "bets" \\ "bet_amount").map(x => x.as[Int])

    for (i <- bet_type_vec.indices)
      vc_bets.addOne(Bet()
        .withBetType(bet_type_vec(i))
        .withPlayerIndex(player_index_vec(i))
        .withBetNumber(bet_number_vec(i))
        .withOddOrEven(bet_odd_or_even_vec(i))
        .withColor(bet_color_vec(i))
        .withBetAmount(bet_amount_vec(i))
      )

    (vc_player.result(), vc_bets.result())
}
