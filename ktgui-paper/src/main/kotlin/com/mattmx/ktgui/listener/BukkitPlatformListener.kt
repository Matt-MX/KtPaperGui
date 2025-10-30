package com.mattmx.ktgui.listener

import com.mattmx.ktgui.impl.BukkitKtGui
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.player.PlayerQuitEvent

class BukkitPlatformListener(
    private val manager: BukkitKtGui
) : Listener {

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return

        val activeGui = manager.getActiveGui(player)
            ?: return

//        activeGui.handleClick(event)
    }

    @EventHandler
    fun onInventoryDrag(event: InventoryDragEvent) {
        val player = event.whoClicked as? Player ?: return

        if (manager.getActiveGui(player) != null) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        val player = event.player as? Player ?: return

//        manager.getActiveGui(player)?.handleClose(event)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        manager.removeActiveGui(event.player)
            ?.close
            ?.apply(event.player)
    }

}