package com.mattmx.ktgui.screen

import kotlin.math.floor

class GuiType(
    val rows: Int?,
    val type: InventoryType
) {
    val middle: Int
        get() = rows?.let { Slots.ofRow(floor(rows / 2.0 + 1).toInt()).middle }
            ?: (type.slots / 2)
    val last = totalSlots - 1
    val first = 0

    infix fun row(n: Int) = rows?.let { Slots.ofRow(n) }
        ?: error("Can only get rows of a row gui type!")

    val totalSlots: Int
        get() = if (rows != null) {
            rows * 9
        } else {
            type.slots
        }

    companion object {
        @JvmStatic
        fun rows(rows: Int) = GuiType(rows, InventoryTypes.GENERICS[rows - 1])

        @JvmStatic
        fun type(type: InventoryType) = GuiType(null, type)
    }
}

data class InventoryType(
    val id: Int,
    val slots: Int,
    val key: String? = null
)

object InventoryTypes {
    // https://minecraft.wiki/w/Minecraft_Wiki:Projects/wiki.vg_merge/Inventory#:~:text=Smithing%20Table-,Types,-%5Bedit%20source
    val GENERIC_9x1 = InventoryType(0, 9, "generic_9x1")
    val GENERIC_9x2 = InventoryType(1, 9 * 2, "generic_9x2")
    val GENERIC_9x3 = InventoryType(2, 9 * 3, "generic_9x3")
    val GENERIC_9x4 = InventoryType(3, 9 * 4, "generic_9x4")
    val GENERIC_9x5 = InventoryType(4, 9 * 5, "generic_9x5")
    val GENERIC_9x6 = InventoryType(5, 9 * 6, "generic_9x6")

    val ANVIL = InventoryType(8, 3)
    val BEACON = InventoryType(9, -1)
    val BLAST_FURNACE = InventoryType(10, -1)
    val BREWING_STAND = InventoryType(11, -1)
    val CRAFTING_TABLE = InventoryType(12, -1)
    val ENCHANTMENT_TABLE = InventoryType(13, -1)
    val FURNACE = InventoryType(14, -1)
    val GRINDSTONE = InventoryType(15, -1)
    val HOPPER = InventoryType(16, 5)
    val LECTERN = InventoryType(17, -1)
    val LOOM = InventoryType(18, -1)
    val VILLAGER = InventoryType(19, -1)
    val SHULKER_BOX = InventoryType(20, -1)
    val SMITHING_TABLE = InventoryType(21, -1)
    val SMOKER = InventoryType(22, -1)
    val CARTOGRAPHY_TABLE = InventoryType(23, -1)
    val STONECUTTER = InventoryType(24, -1)

    val CUSTOM_HOTBAR = InventoryType(-1, 9)

    val GENERICS = arrayOf(
        GENERIC_9x1,
        GENERIC_9x2,
        GENERIC_9x3,
        GENERIC_9x4,
        GENERIC_9x5,
        GENERIC_9x6,
    )
}