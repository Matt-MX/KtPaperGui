package com.mattmx.ktgui.screen

import kotlin.math.floor

class GuiType(
    val rows: Int?,
    val type: InventoryType
) {
    val middle = rows?.let { Slots.ofRow(floor(rows / 2.0).toInt()).middle }
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

    val ANVIL = InventoryType(8, 3)
    val BEACON = InventoryType(9, -1)
    val BLAST_FURNACE = InventoryType(10, -1)
    val BREWING_STAND = InventoryType(11, -1)
    val CRAFTING_TABLE = InventoryType(12, -1)
    val ENCHANTMENT_TABLE = InventoryType(13, -1)
    val FURNACE = InventoryType(14, -1)
    val GRINDSTONE = InventoryType(15, -1)
    val HOPPER = InventoryType(16, -1)
    val LECTERN = InventoryType(17, -1)
    val LOOM = InventoryType(18, -1)
    val VILLAGER = InventoryType(19, -1)
    val SHULKER_BOX = InventoryType(20, -1)
    val SMITHING_TABLE = InventoryType(21, -1)
    val SMOKER = InventoryType(22, -1)
    val CARTOGRAPHY_TABLE = InventoryType(23, -1)
    val STONECUTTER = InventoryType(24, -1)

    val GENERICS = arrayOf(
        GENERIC_9x1,
        GENERIC_9x2,
        GENERIC_9x3,
        GENERIC_9x4,
        GENERIC_9x5,
        GENERIC_9x6,
    )
}