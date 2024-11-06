package com.mattmx.ktgui.components.screen.slot

import com.mattmx.ktgui.components.screen.GuiScreen

class Slots(
    private val parent: GuiScreen
) {
    init {
        if (parent.type != null) {
            error("The Slots feature can only be used for generic GUI interfaces, not InventoryType.")
        }
    }

    val first: Int
        get() = parent.first()
    val middle: Int
        get() = parent.middle()
    val last: Int
        get() = parent.last()

    infix fun row(i: Int): Row {
        return Row(i)
    }

    fun of(x: Int, y: Int): Int {
        return Row(y).column(x)
    }

    class Row(
        val row: Int
    ) {
        val middle = ROW_SIZE * row + HALF_ROW
        val first = ROW_SIZE * row
        val last = ROW_SIZE * (row + 1) - 1

        fun column(c: Int) = ROW_SIZE * row + c

        companion object {
            const val ROW_SIZE = 9
            const val HALF_ROW = 4
        }
    }
}