package Roulette.controller

import Roulette.controller.controllerComponent.controllerBaseImpl.Controller
import Roulette.model.{Player, Bet, PlayerUpdate}
import Roulette.util.{Command, UndoManager}

class PutCommand(player_update: PlayerUpdate, controller: Controller) extends Command {

  private val old_players: Vector[Player] =
    controller.players

  private val old_bets: Vector[Bet] =
    controller.bets

  private val old_randomNumber: Int =
    controller.randomNumber
  override def doStep(): Unit =
    controller.updatePlayer(player_update.player_index, player_update.money)
  override def undoStep(): Unit =
    controller.players = old_players
    controller.bets = old_bets
    controller.randomNumber = old_randomNumber
  override def redoStep(): Unit =
    controller.updatePlayer(player_update.player_index, player_update.money)
    controller.bets = player_update.bets
    controller.randomNumber = player_update.randomNumber
}
