package com.mattmx.ktgui.components.button

import com.mattmx.ktgui.GuiManager
import com.mattmx.ktgui.extensions.getOpenGui
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
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
) : Event(), Cancellable {
    val slot = event.rawSlot
    val currentGui = player.getOpenGui()
    private var callbackShouldContinue = true
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
    override fun isCancelled() = !shouldContinueCallback()

    override fun setCancelled(cancel: Boolean) = shouldContinueCallback(!cancel)

    fun forceClose() {
        GuiManager.clearGui(player)
        player.closeInventory()
    }
}