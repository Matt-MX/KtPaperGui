package com.mattmx.ktgui.components.button

import com.mattmx.ktgui.item.DslIBuilder
import com.mattmx.ktgui.item.builder
import com.mattmx.ktgui.item.itemBuilder
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

data class ButtonClickedEvent(
    val player: Player,
    val event: InventoryClickEvent,
    val button: IGuiButton?
) : Event() {
    val slot = event.rawSlot

    inline fun item(builder: DslIBuilder.() -> Unit) {
        val item = ((event.currentItem ?: ItemStack(Material.STONE)) builder builder).build()
        // Send change to player
        player.openInventory.setItem(event.rawSlot, item)
    }

    override fun getHandlers() = HandlerList()
}