package com.mattmx.ktguis

import com.mattmx.ktguis.components.IGuiScreen
import com.mattmx.ktguis.extensions.getOpenGui
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.UUID

object GuiManager : Listener {
    val guis = hashMapOf<String, IGuiScreen>()
    val players = hashMapOf<UUID, IGuiScreen>()

    fun register(id: String, gui: IGuiScreen) {
        guis[id] = gui
    }

    @EventHandler
    fun click(e: InventoryClickEvent) {
        (e.whoClicked as Player).getOpenGui()?.click(e)
    }

    @EventHandler
    fun close(e: InventoryCloseEvent) {
        (e.player as Player).getOpenGui()?.close(e)
    }

    @EventHandler
    fun quit(e: PlayerQuitEvent) {
        val gui = e.player.getOpenGui()
        gui?.let {
            gui.quit(e)
            gui.destroy()
            players.remove(e.player.uniqueId)
        }

    }

    @EventHandler
    fun chat(e: AsyncPlayerChatEvent) {
        e.player.getOpenGui()?.chat(e)
    }

    @EventHandler
    fun move(e: PlayerMoveEvent) {
        e.player.getOpenGui()?.move(e)
    }
}