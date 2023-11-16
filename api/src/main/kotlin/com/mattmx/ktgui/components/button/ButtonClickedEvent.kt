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
 * ! Not to be used in the Bukkit Event System !
 *
 * @param player who clicked
 * @param event underlying Bukkit event
 * @param button button that was clicked
 * @param itemClicked [ItemStack] that was clicked
 */
data class ButtonClickedEvent<T : IGuiButton<*>>(
    val player: Player,
    val event: InventoryClickEvent,
    val itemClicked: ItemStack? = event.currentItem
) : Event() {
    val slot = event.rawSlot
    val currentGui = player.getOpenGui()
    private var callbackShouldContinue = false
    lateinit var button: T

    fun isButton() = ::button.isInitialized

    override fun getHandlers() = HandlerList()

    /**
     * Any "super" callbacks will be called unless specified.
     *
     * This includes gui callbacks.
     *
     * @param callback should we continue further callbacks
     */
    fun shouldContinueCallback(callback: Boolean) {
        callbackShouldContinue = callback
    }

    fun shouldContinueCallback() = callbackShouldContinue
}