package Roulette.controller

import Roulette.controller.controllerComponent.controllerBaseImpl.Controller
import Roulette.controller.Command
import Roulette.core.{Player, Bet}
import Roulette.utility.Event
import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class PutCommand(playerId: UUID, money: Int, randomNumber: Int, bets: Vector[Bet], controller: Controller)(implicit ec: ExecutionContext) extends Command {

  private var oldState: Option[(Vector[Player], Int, Vector[Bet])] = None

  private def backupState(): Unit = {
    val currentPlayers = controller.getPlayers
    val currentBets = controller.getBets
    oldState = Some((currentPlayers, controller.randomNumber, currentBets))
  }

  override def doStep(): Unit = {
    backupState()
    controller.updatePlayer(playerId, money)
    controller.setBets(bets)
    controller.randomNumber = randomNumber
    controller.notifyObservers(Event.UPDATE)
  }

  override def undoStep(): Unit = {
    oldState.foreach {
      case (players, oldRandomNumber, oldBets) =>
        controller.setPlayers(players)
        controller.setBets(oldBets)
        controller.randomNumber = oldRandomNumber
        controller.notifyObservers(Event.UPDATE)
    }
  }

  override def redoStep(): Unit = doStep()  // Simply re-invoke the doStep to reapply the updates
}
