package Roulette.core

import Roulette.core.Bet

import java.util.UUID

case class PlayerUpdate(player_id: UUID, money: Int, bets: Vector[Bet], randomNumber:Int)
