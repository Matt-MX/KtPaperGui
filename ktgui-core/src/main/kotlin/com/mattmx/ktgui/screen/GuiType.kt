package com.mattmx.ktgui.screen

class GuiType(
    val rows: Int?,
    val type: InventoryType?
) {
    val middle = rows?.let { Slots.ofRow(rows).middle }
        ?: error("Can only get middle slot of a row gui type!")

    fun getTotalSlots(): Int {
        if (rows != null) {
            return rows * 9
        } else if (type != null) {
            return type.slots
        } else error("GuiType is neither of rows or type!")
    }

    companion object {
        @JvmStatic
        fun ofRows(rows: Int) = GuiType(rows, null)

        @JvmStatic
        fun ofType(type: InventoryType) = GuiType(null, type)
    }
}

data class InventoryType(
    val id: Int,
    val slots: Int
)

object InventoryTypes {
    val HOPPER = InventoryType(0, 5)
}