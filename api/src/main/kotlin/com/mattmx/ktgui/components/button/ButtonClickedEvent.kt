package com.mattmx.ktgui.components.button

import com.mattmx.ktgui.extensions.getOpenGui
import com.mattmx.ktgui.item.DslIBuilder
import com.mattmx.ktgui.item.builder
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

/**
 * Used to contain the [InventoryClickEvent] and some extra data.
 *
 * Not to be used in the Bukkit Event System !
 *
 * @param player who clicked
 * @param event underlying Bukkit event
 * @param button button that was clicked
 * @param itemClicked [ItemStack] that was clicked
 */
data class ButtonClickedEvent(
    val player: Player,
    val event: InventoryClickEvent,
    val button: IGuiButton?,
    val itemClicked: ItemStack?
) : Event() {
    // I added this since event.slot is really just not useful and can be quite confusing.
    val slot = event.rawSlot
    val currentGui = player.getOpenGui()
    private var callbackShouldContinue = false

    // todo maybe remove, it's a bit of a niche thing to add?
    // new system will handle this anyway so
    inline fun changeItem(builder: DslIBuilder.() -> Unit) {
        val item = ((event.currentItem ?: ItemStack(Material.STONE)) builder builder).build()
        // Send change to player
        player.openInventory.setItem(event.rawSlot, item)
    }

    override fun getHandlers() = HandlerList()

    fun shouldContinueCallbacks(callback: Boolean) {
        callbackShouldContinue = callback
    }

    fun shouldContinueCallbacks() = callbackShouldContinue
}