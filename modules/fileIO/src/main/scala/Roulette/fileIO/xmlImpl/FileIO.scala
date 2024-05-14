package Roulette.fileIO.xmlImpl

import Roulette.fileIO.FileIOInterface
import Roulette.core.{Bet, Player}

import scala.collection.immutable.VectorBuilder
import java.io.{File, PrintWriter}
import scala.xml.PrettyPrinter
import scala.xml.XML
import java.nio.file.{Files, Paths}
import java.util.UUID

class FileIO extends FileIOInterface {

  override def save(vector_player: Vector[Player], vector_bet: Vector[Bet]): Unit =
    saveString(vector_player, vector_bet)

  private def saveString(vector_player: Vector[Player], vector_bet: Vector[Bet]): Unit = {
    val path = Paths.get("saves/")
    if (!Files.exists(path))
      Files.createDirectory(path)
    val pw = new PrintWriter(new File(path.toString, "savefile.xml"))
    val prettyPrinter = new PrettyPrinter(120, 4)
    val xml = prettyPrinter.format(vectorsToXml(vector_player, vector_bet))
    pw.write(xml)
    pw.close()
  }

  private def vectorsToXml(vector_player: Vector[Player], vector_bet: Vector[Bet]) =
    <roulette>
      <players>
        {vector_player.map(player =>
        <player>
          <id>
            {player.id}
          </id>
          <available_money>
            {player.available_money}
          </available_money>
        </player>
      )}
      </players>
      <bets>
        {vector_bet.map(bet =>
        <bet>
          <id>
            {bet.id.getOrElse(UUID.randomUUID())}
          </id>{/* Generate new UUID if not present */}<bet_type>
          {bet.bet_type.getOrElse("")}
        </bet_type>
          <player_index>
            {bet.player_id.getOrElse(0)}
          </player_index>
          <bet_number>
            {bet.bet_number.getOrElse(0)}
          </bet_number>
          <bet_odd_or_even>
            {bet.bet_odd_or_even.getOrElse("")}
          </bet_odd_or_even>
          <bet_color>
            {bet.bet_color.getOrElse("")}
          </bet_color>
          <bet_amount>
            {bet.bet_amount.getOrElse(0)}
          </bet_amount>
          <random_number>
            {bet.random_number.getOrElse(0)}
          </random_number>
        </bet>
      )}
      </bets>
    </roulette>

  override def load(): (Vector[Player], Vector[Bet]) = {
    val file = scala.xml.XML.loadFile("saves/savefile.xml")
    val playerNodes = (file \ "players" \ "player")
    val players = playerNodes.map(node =>
      Player(
        id = UUID.fromString((node \ "id").text.trim),
        available_money = (node \ "available_money").text.trim.toInt
      )
    ).toVector

    val betNodes = (file \ "bets" \ "bet")
    val bets = betNodes.map(node =>
      Bet(
        id = Some(UUID.fromString((node \ "id").text.trim)),
        bet_type = Some((node \ "bet_type").text.trim),
        player_id = Some(UUID.fromString((node \ "player_id").text.trim)),
        bet_number = Some((node \ "bet_number").text.trim.toInt),
        bet_odd_or_even = Some((node \ "bet_odd_or_even").text.trim),
        bet_color = Some((node \ "bet_color").text.trim),
        bet_amount = Some((node \ "bet_amount").text.trim.toInt),
        random_number = Some((node \ "random_number").text.trim.toInt)
      )
    ).toVector

    (players, bets)
  }

}
