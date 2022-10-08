package com.mattmx.ktgui.components.button

import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

data class ButtonClickedEvent(
    val player: Player,
    val event: InventoryClickEvent,
    val button: IGuiButton
)