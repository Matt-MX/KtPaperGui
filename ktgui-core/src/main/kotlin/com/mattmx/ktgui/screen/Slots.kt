package com.mattmx.ktgui.screen

object Slots {
    fun ofPosition(x: Int, y: Int): Int {
        return Row(y).column(x)
    }

    fun ofRow(row: Int) = Row(row)
}

class Row(
    val row: Int
) {
    val middle = ROW_SIZE * (row - 1) + HALF_ROW
    val first = ROW_SIZE * (row - 1)
    val last = ROW_SIZE * row - 1

    fun column(c: Int) = ROW_SIZE * row + c

    companion object {
        const val ROW_SIZE = 9
        const val HALF_ROW = 4
    }
}