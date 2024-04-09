package Roulette.core

import Roulette.core.Bet

case class PlayerUpdate(player_index: Int, money: Int, bets: Vector[Bet], randomNumber:Int)
