package Roulette.fileIO.xmlImpl

import Roulette.fileIO.FileIOInterface
import Roulette.core.{Bet, Player}

import scala.collection.immutable.VectorBuilder
import java.io.{File, PrintWriter}
import scala.xml.PrettyPrinter
import scala.xml.XML
import java.nio.file.{Files, Paths}

class FileIO extends FileIOInterface {

  override def save(vector_player: Vector[Player], vector_bet: Vector[Bet]): Unit =
    saveString(vector_player, vector_bet)

  private def saveString(vector_player: Vector[Player], vector_bet: Vector[Bet]): Unit =
    val path = Paths.get("saves/")
    if (!Files.exists(path))
      Files.createDirectory(path)
    val pw = new PrintWriter(new File(path.toString, "savefile.xml"))
    val prettyPrinter = new PrettyPrinter(120, 4)
    val xml = prettyPrinter.format(vectorsToXml(vector_player, vector_bet))
    pw.write(xml)
    pw.close()

  private def vectorsToXml(vector_player: Vector[Player], vector_bet: Vector[Bet]) =
    <roulette>
      <players>
        {for { i <- vector_player.indices } yield {
        <player index = {i.toString}>
              {vector_player(i).getAvailableMoney}
        </player>
      }
    }
      </players>
      <bets>
        {for { i <- vector_bet.indices } yield {
        <bet>
              <bet_type>{vector_bet(i).bet_type}</bet_type>
              <player_index>{vector_bet(i).player_index}</player_index>
              <bet_number>{vector_bet(i).bet_number}</bet_number>
              <bet_odd_or_even>{vector_bet(i).bet_odd_or_even}</bet_odd_or_even>
              <bet_color>{vector_bet(i).bet_color}</bet_color>
              <bet_amount>{vector_bet(i).bet_amount}</bet_amount>
        </bet>
      }
    }
      </bets>
    </roulette>

  override def load(): (Vector[Player], Vector[Bet]) =
    val vc_player = VectorBuilder[Player]
    val vc_bets = VectorBuilder[Bet]
    val file = scala.xml.XML.loadFile("saves/savefile.xml")
    val playerNodes = (file \ "players" \\ "player")
    for (player <- playerNodes)
      vc_player.addOne(Player(player.text.trim.toInt))
    val betNodes = (file \ "bets" \\ "bet")
    for (bet <- betNodes)
      vc_bets.addOne(
        Bet()
          .withBetType((bet \ "bet_type").text)
          .withPlayerIndex((bet \ "player_index").text.trim.toInt)
          .withBetNumber((bet \ "bet_number").text.trim.toInt)
          .withOddOrEven((bet \ "bet_odd_or_even").text)
          .withColor((bet \ "bet_color").text)
          .withBetAmount((bet \ "bet_amount").text.trim.toInt))
    (vc_player.result(), vc_bets.result())

}
