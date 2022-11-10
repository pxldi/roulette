/*package Roulette.controller


class Controller(player: Player) extends Observable :
  val undoManager = new UndoManager

  def setMoney(playerIndex: Int, money: Int) =
    //field = doThis
    player.players(playerIndex) = player.players(playerIndex) + gewinn
    notifyObservers

  def bet(move: Move): Field =
    undoManager.doStep(field, PutCommand(move))

    def undo: Field =
      undoManager.undoStep(field)

  def redo: Field =
    undoManager.redoStep(field)
*/

package Roulette
package controller

import Roulette.model.Player
import Roulette.util.Observable
import Roulette.{Command, UndoManager}

case class Controller(val playerCount: Int) extends Observable:
  def put: Player = {
    val player = Player(playerCount)
    notifyObservers
    player
  }