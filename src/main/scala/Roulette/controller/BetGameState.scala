package Roulette.controller

class BetGameState(protected val controller: Controller) extends GameState{
    def stateToString(): String = {
        val retval = "Betting possible"
        retval
    }
}
