package Roulette.model

case class PlayerUpdate(player_index: Int, money: Int, bets: Vector[Bet], randomNumber:Int)
