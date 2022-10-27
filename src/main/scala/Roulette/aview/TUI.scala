package Roulette.aview

class TUI {
    def processInput(input: String): Unit = {
        input match
            case "h" =>
                println("Welcome to Roulette!\nYou can bet by writing 'b'!")
            case "b" =>
                println("Bet")
            case "q" =>
                println("Quitting...")
    }
}