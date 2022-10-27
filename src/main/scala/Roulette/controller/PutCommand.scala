/*
package de.htwg.se.tictactoe
package controller

import model.Field
import model.Move
import model.Stone
import util.Command
import util.UndoManager

class PutCommand(move: Move) extends Command[Field]:
  override def noStep(field: Field): Field = field
  override def doStep(field: Field): Field = field.put(move.stone, move.x, move.y)
  override def undoStep(field: Field): Field = field.put(Stone.Empty, move.x, move.y)
  override def redoStep(field: Field): Field = field.put(move.stone, move.x, move.y)
  */