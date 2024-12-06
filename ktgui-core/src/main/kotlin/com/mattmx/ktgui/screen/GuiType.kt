package com.mattmx.ktgui.screen

class GuiType(
    val rows: Int?,
    val type: InventoryType
) {
    val middle = rows?.let { Slots.ofRow(rows).middle }
        ?: error("Can only get middle slot of a row gui type!")
    val last = getTotalSlots() - 1
    val first = 0

    fun getTotalSlots(): Int {
        return if (rows != null) {
            rows * 9
        } else {
            type.slots
        }
    }

    companion object {
        @JvmStatic
        fun ofRows(rows: Int) = GuiType(rows, InventoryTypes.GENERICS[rows - 1])

        @JvmStatic
        fun ofType(type: InventoryType) = GuiType(null, type)
    }
}

data class InventoryType(
    val id: Int,
    val slots: Int
)

object InventoryTypes {
    // https://minecraft.wiki/w/Minecraft_Wiki:Projects/wiki.vg_merge/Inventory#:~:text=Smithing%20Table-,Types,-%5Bedit%20source
    val GENERIC_9x1 = InventoryType(0, 9)
    val GENERIC_9x2 = InventoryType(1, 9 * 2)
    val GENERIC_9x3 = InventoryType(2, 9 * 3)
    val GENERIC_9x4 = InventoryType(3, 9 * 4)
    val GENERIC_9x5 = InventoryType(4, 9 * 5)
    val GENERIC_9x6 = InventoryType(5, 9 * 6)
    val GENERICS = arrayOf(
        GENERIC_9x1,
        GENERIC_9x2,
        GENERIC_9x3,
        GENERIC_9x4,
        GENERIC_9x5,
        GENERIC_9x6,
    )
}