package com.mattmx.ktgui.components.screen.slot

import com.mattmx.ktgui.components.button.GuiButton
import com.mattmx.ktgui.components.screen.GuiScreen
import kotlin.math.floor

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
    val center: Int
        get() = if (parent.type == null) {
            row(floor(parent.rows * 0.5).toInt()).middle
        } else floor(parent.totalSlots() * 0.5).toInt()
    val last: Int
        get() = parent.last()

    infix fun row(i: Int): Row {
        return Row(i)
    }

    fun of(x: Int, y: Int): Int {
        return Row(y).column(x)
    }

    fun after(sibling: GuiButton<*>): Int {
        return (sibling.slots().maxOrNull() ?: (first - 1)) + 1
    }

    fun before(sibling: GuiButton<*>): Int {
        return (sibling.slots().minOrNull() ?: (last + 1)) - 1
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