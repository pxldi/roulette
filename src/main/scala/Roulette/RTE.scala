package Roulette

import scala.io.StdIn.readLine
import scala.util.Random

@main def main(): Unit =
    val playercount : Int = readLine("Anzahl Spieler: " ).toInt
    println("Spieleranzahl: " + playercount )

    val r = new Random()
    val randZahl : Int = r.nextInt(37)


    println()
    println("Willst du eine Zahl (z), gerade oder ungerade (g), Farbe (f), \n" +
      "1-18 (1h), 9-36 (2h), 1-12 (12), 13-24 (24), 25-36 (36), \n" +
      "Reihe 1 4 7 X+3 bis 34 (r1), Reihe 2 5 8 X+3,bis 35 (r2), Reihe 3 6 9 12,X+3 bis 36 (r3) setzen?")
    readLine() match
        case "z" =>
            val einsatzZahl : Int = readLine("Auf welches Feld wollen sie ihren Einsatz setzen? ").toInt

            println("Beim Drehen vom Roulette kam eine: " + randZahl + " heraus")

            if(randZahl == einsatzZahl)
                println("Sie haben gewonnen")
            else
                println("Sie haben leider nicht gewonnen")
        case "g" =>
            println("Willst du auf gerade(g) oder ungerade(u) setzten? ")
                readLine() match
                    case "g" =>
                        println("Beim Drehen vom Roulette kam eine: " + randZahl + " heraus")
                        if(randZahl % 2 == 0)
                            println("Sie haben gewonnen")
                        else
                            println("Sie haben leider nicht gewonnen")

                    case "u" =>
                        println("Beim Drehen vom Roulette kam eine: " + randZahl + " heraus")
                        if(randZahl % 2 != 0)
                            println("Sie haben gewonnen")
                        else
                            println("Sie haben leider nicht gewonnen")
        case "f" =>
            var roteZahlen = Array(1,3,5,7,9,12,14,16,18,19,21,23,25,27,30,32,34,36)
            var schwarzeZahlen = Array(2,4,6,8,10,11,13,15,17,20,22,24,26,28,29,31,33,35)

            println("Willst du auf rot(r) oder schwarz(s) setzten? ")
            readLine() match
                case "r" =>
                    println("Beim Drehen vom Roulette kam eine: " + randZahl + " heraus")
                    if(roteZahlen.contains(randZahl))
                        println("Sie haben gewonnen")
                    else
                        println("Sie haben leider nicht gewonnen")

                case "s" =>
                    println("Beim Drehen vom Roulette kam eine: " + randZahl + " heraus")
                    if(schwarzeZahlen.contains(randZahl))
                        println("Sie haben gewonnen")
                    else
                        println("Sie haben leider nicht gewonnen")

        case "1h" =>
            println("Beim Drehen vom Roulette kam eine: " + randZahl + " heraus")
            if(randZahl <= 18)
                println("Sie haben gewonnen")
            else
                println("Sie haben leider nicht gewonnen")

        case "2h" =>
            println("Beim Drehen vom Roulette kam eine: " + randZahl + " heraus")
            if(randZahl <= 36 && randZahl >= 19)
                println("Sie haben gewonnen")
            else
                println("Sie haben leider nicht gewonnen")

        case "12" =>
            println("Beim Drehen vom Roulette kam eine: " + randZahl + " heraus")
            if(randZahl <= 12)
                println("Sie haben gewonnen")
            else
                println("Sie haben leider nicht gewonnen")
        case "24" =>
            println("Beim Drehen vom Roulette kam eine: " + randZahl + " heraus")
            if(randZahl <= 24 && randZahl >= 13)
                println("Sie haben gewonnen")
            else
                println("Sie haben leider nicht gewonnen")
        case "36" =>
            println("Beim Drehen vom Roulette kam eine: " + randZahl + " heraus")
            if(randZahl <= 36 && randZahl >= 25)
                println("Sie haben gewonnen")
            else
                println("Sie haben leider nicht gewonnen")
        case "r1" =>
            var r1Zahlen = Array(1, 4, 7, 10, 13, 16, 19, 22, 25, 28, 31, 34)
            println("Beim Drehen vom Roulette kam eine: " + randZahl + " heraus")
            println("Beim Drehen vom Roulette kam eine: " + randZahl + " heraus")
            if(r1Zahlen.contains(randZahl))
                println("Sie haben gewonnen")
            else
                println("Sie haben leider nicht gewonnen")
        case "r2" =>
            var r2Zahlen = Array(2, 5, 8, 11, 14, 17, 20, 23, 26, 29, 32, 35)
            println("Beim Drehen vom Roulette kam eine: " + randZahl + " heraus")
            if(r2Zahlen.contains(randZahl))
                println("Sie haben gewonnen")
            else
                println("Sie haben leider nicht gewonnen")
        case "r3" =>
            var r3Zahlen = Array(3, 6, 9, 12, 15, 18, 21, 24, 27, 30, 33, 36)
            println("Beim Drehen vom Roulette kam eine: " + randZahl + " heraus")
            if(r3Zahlen.contains(randZahl))
                println("Sie haben gewonnen")
            else
                println("Sie haben leider nicht gewonnen")
        case "2n" =>
        case "3n" =>
        case "4n" =>
        case "6n" =>

