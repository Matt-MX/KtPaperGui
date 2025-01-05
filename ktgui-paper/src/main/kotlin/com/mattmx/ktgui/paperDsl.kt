package com.mattmx.ktgui

import com.mattmx.ktgui.impl.PaperGuiButton
import com.mattmx.ktgui.impl.PaperGuiScreen
import com.mattmx.ktgui.screen.GuiType
import com.mattmx.ktgui.screen.InventoryType
import net.kyori.adventure.text.Component
import org.bukkit.Material

fun gui(title: Component, type: GuiType, block: PaperGuiScreen<*>.() -> Unit) =
    PaperGuiScreen(type, title).apply(block)

fun button(type: Material, block: PaperGuiButton<*>.() -> Unit) =
    PaperGuiButton(type).apply(block)

val InventoryType.bukkit
    get() = org.bukkit.event.inventory.InventoryType
        .entries
        .firstOrNull { bukkitType -> bukkitType.menuType?.key()?.asString() == key }
        ?: error("No Bukkit type of InventoryType found for $this")