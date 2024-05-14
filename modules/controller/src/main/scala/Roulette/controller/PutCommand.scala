package Roulette.controller

import Roulette.controller.controllerComponent.controllerBaseImpl.Controller
import Roulette.core.{Player, Bet}
import Roulette.utility.{Command, Event}
import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class PutCommand(playerId: UUID, money: Int, randomNumber: Int, bets: Vector[Bet], controller: Controller)(implicit ec: ExecutionContext) extends Command {

  private var oldState: Option[(Vector[Player], Int, Vector[Bet])] = None

  private def backupState(): Future[Unit] = {
    controller.getPlayers().flatMap { players =>
      controller.getBets().map { currentBets =>
        oldState = Some((players, controller.randomNumber, currentBets))
      }
    }
  }

  override def doStep(): Unit = {
    backupState().onComplete {
      case Success(_) =>
        val updateFuture = controller.updatePlayer(playerId, money).map(_ => ())
        val betsUpdateFuture = controller.setBets(bets)

        // Update the random number
        controller.randomNumber = randomNumber

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
      case (players, oldRandomNumber, oldBets) =>
        controller.setPlayers(players).flatMap { _ =>
          controller.setBets(oldBets).map { _ =>
            controller.randomNumber = oldRandomNumber  // Restore the random number
            controller.notifyObservers(Event.UPDATE)
          }
        }
    }
  }

  override def redoStep(): Unit = doStep()  // Simply re-invoke the doStep to reapply the updates
}
