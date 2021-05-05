package minesweeper

import java.lang.Exception

data class Coordinate(val x: Int, val y: Int) {
    val string: String = "$x$y"
}

class MinesWeeper(private val width: Int = 9, private val height: Int = 9) {
    private val visibleField: MutableList<CharArray> = mutableListOf()
    private val hiddenField: MutableList<CharArray> = mutableListOf()
    private val bombs: MutableList<Coordinate> = mutableListOf()
    private val marks: MutableList<Coordinate> = mutableListOf()
    private val minesCountMax: Int
    private var notOpenedCells = width * height
    private var wasted = false

    // init field with dots, ask user to enter bombs quantity
    init {
        for (i in 1..height) {
            visibleField.add(CharArray(width) { _ -> '.' })
            hiddenField.add(CharArray(width) { _ -> '.' })
        }
        print("How many mines do you want on the field?")
        minesCountMax = readLine()!!.toInt()
        printField()
    }
    // places bombs on random cells, but not to first chosen cell
    private fun placeBombs(xFirst: Int, yFirst: Int) {
        var minesCountRest = minesCountMax
        while (minesCountRest > 0)
        {
            val y = (0 until height).random()
            val x = (0 until width).random()
            if (hiddenField[y][x] == 'X' || x == xFirst && y == yFirst) continue
            minesCountRest--
            hiddenField[y][x] = 'X'
            bombs.add(Coordinate(x, y))
            for (j in maxOf(0, y - 1)..minOf(y + 1, height - 1))
                for (i in maxOf(0, x - 1)..minOf(x + 1, width - 1))
                    when(hiddenField[j][i]) {
                        '.' -> hiddenField[j][i] = '1'
                        in '1'..'7' -> hiddenField[j][i]++
                    }
        }
        bombs.sortBy { it.string }
    }
    // DEPRECATED hides bombs from the field
    private fun hideBombs() {
        for (j in visibleField.indices)
            for (i in visibleField[j].indices)
                if (visibleField[j][i] == 'X') visibleField[j][i] = '.'
    }
    // prints field with numbered border
    private fun printField() {
        println("""
             |123456789|
            -|---------|            
            """.trimIndent())
        for (j in visibleField.indices)
            println("${j + 1}|${visibleField[j].joinToString("")}|")
        println("-|---------|")
    }
    // adds or removes '*'
    private fun markAddOrRemove(c: Coordinate) {
        if (marks.contains(c)) {
            marks.remove(c)
            visibleField[c.y][c.x] = '.'
        }
        else {
            marks.add(c)
            visibleField[c.y][c.x] = '*'
        }
        marks.sortBy { it.string }
    }
    // checks if the WIN conditions are true
    private fun checkWin() :Boolean {
        if (bombs == marks || notOpenedCells == minesCountMax) return true
        return false
    }
    // checks if cell contains a number
    private fun cellIsNumbered(x: Int, y: Int) = hiddenField[y][x] in '1'..'8'
    // opening first cell and place bombs to other cells
    private fun firstMove() {
        do {
            print("Set/unset mines marks or claim a cell as free:")
            val (i, j, command) = readLine()!!.split(" ")
            try {
                val x = i.toInt()
                val y = j.toInt()
                when(command) {
                    "free" -> placeBombs(x - 1, y - 1).also { openCell(x - 1, y - 1) }
                    "mine" -> markAddOrRemove(Coordinate(x - 1, y - 1))
                }
                printField()
            }
            catch (e: Exception) {
                continue
            }
        } while (command != "free")
    }
    // opens cell and cells around if it is empty
    private fun openCell(x: Int, y: Int) {
        notOpenedCells--
        if (marks.contains(Coordinate(x, y))) markAddOrRemove(Coordinate(x, y))
        if (cellIsNumbered(x, y)) visibleField[y][x] = hiddenField[y][x]
        if (hiddenField[y][x] == 'X') {
            wasted = true
            visibleField[y][x] = hiddenField[y][x]
        }
        if (hiddenField[y][x] == '.' && visibleField[y][x] == '.') {
            visibleField[y][x] = '/'
            for (j in maxOf(0, y - 1)..minOf(y + 1, height - 1))
                for (i in maxOf(0, x - 1)..minOf(x + 1, width - 1))
                    if (hiddenField[j][i]!='X') openCell(i, j)
        }
    }
    fun play() {
        firstMove()
        while (true) {
            print("Set/unset mines marks or claim a cell as free:")
            val (i, j, command) = readLine()!!.split(" ")
            val x = i.toInt()
            val y = j.toInt()
            if (visibleField[y - 1][x - 1] in '1'..'8') {
                println("There is a number here!")
                continue
            }
            when(command) {
                "mine" -> markAddOrRemove(Coordinate(x - 1, y - 1))
                "free" -> openCell(x - 1, y - 1)
            }
            printField()
            if (wasted) {
                println("You stepped on a mine and failed!")
                break
            }
            if (checkWin()) {
                println("Congratulations! You found all the mines!")
                break
            }
        }
    }
}
fun main() {
    val game = MinesWeeper()
    game.play()
}