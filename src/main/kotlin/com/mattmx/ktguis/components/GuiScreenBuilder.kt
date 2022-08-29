package com.mattmx.ktguis.components

import org.bukkit.event.inventory.InventoryType

class GuiScreenBuilder(
    title: String = "null",
    rows: Int = 1,
    type: InventoryType? = null,
) : GuiScreen(title, rows, type) {

}