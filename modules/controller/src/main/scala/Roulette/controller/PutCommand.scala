package Roulette.controller

import Roulette.controller.controllerComponent.controllerBaseImpl.Controller
import Roulette.core.{PlayerUpdate, Player, Bet}
import Roulette.utility.Command
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class PutCommand(player_update: PlayerUpdate, controller: Controller)(implicit ec: ExecutionContext) extends Command {

  private var oldState: Option[(Vector[Player], Int, Vector[Bet])] = None

  private def backupState(): Future[Unit] = {
    controller.getPlayers().flatMap { players =>
      controller.getBets().map { bets =>
        oldState = Some((players, controller.randomNumber, bets))
      }
    }
  }

  override def doStep(): Unit = {
    backupState().onComplete {
      case Success(_) =>
        val updateFuture = controller.updatePlayer(player_update.player_id, player_update.money)
        val betsUpdateFuture = controller.updateBets(player_update.bets)

        // Handle random number update, assuming it needs to be set
        controller.randomNumber = player_update.randomNumber

        // Ensure all futures complete before considering the step done
        Future.sequence(List(updateFuture, betsUpdateFuture)).onComplete {
          case Success(_) => println("Update and bets successfully processed.")
          case Failure(exception) => println("Failed to process update or bets: " + exception.getMessage)
        }

      case Failure(exception) =>
        println("Failed to backup state: " + exception.getMessage)
    }
  }

  override def undoStep(): Unit = {
    oldState.foreach {
      case (players, randomNumber, bets) =>
        controller.setPlayers(players)  // Assuming this restores players
        controller.setBets(bets)  // Assuming this restores bets
        controller.randomNumber = randomNumber  // Restore the random number
    }
  }

  override def redoStep(): Unit = doStep()  // Simply re-invoke the doStep to reapply the updates
}
