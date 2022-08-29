package com.mattmx.ktgui

import com.mattmx.ktgui.components.screen.IGuiScreen
import com.mattmx.ktgui.extensions.getOpenGui
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import java.util.UUID

object GuiManager : Listener {
    val guis = hashMapOf<String, IGuiScreen>()
    val players = hashMapOf<UUID, IGuiScreen>()

    fun init(plugin: JavaPlugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin)
    }

    fun register(id: String, gui: IGuiScreen) {
        guis[id] = gui
    }

    @EventHandler
    fun click(e: InventoryClickEvent) {
        (e.whoClicked as Player).getOpenGui()?.let {
            e.isCancelled = true
            it.click(e)
        }
    }

    @EventHandler
    fun close(e: InventoryCloseEvent) {
        val gui = (e.player as Player).getOpenGui()
        gui?.let {
            gui.close(e)
            players.remove(e.player.uniqueId)
        }
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