package com.mattmx.ktgui

import com.mattmx.ktgui.components.screen.IGuiScreen
import com.mattmx.ktgui.conversation.ConversationAbandonListener
import com.mattmx.ktgui.extensions.getOpenGui
import com.mattmx.ktgui.utils.GitUpdateChecker
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import java.util.UUID
import kotlin.jvm.internal.Intrinsics.Kotlin

object GuiManager : Listener {
    val guis = hashMapOf<String, IGuiScreen>()
    val players = hashMapOf<UUID, IGuiScreen>()
    lateinit var owningPlugin: JavaPlugin

    fun init(plugin: JavaPlugin) {
        this.owningPlugin = plugin
        Bukkit.getPluginManager().registerEvents(this, plugin)
        KotlinBukkitGui.version = ""
        KotlinBukkitGui.papi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null
        KotlinBukkitGui.protocollib = Bukkit.getPluginManager().getPlugin("ProtocolLib") != null
    }

    fun getPlayers(gui: IGuiScreen) = players.filter { it.value == gui }.keys
    inline fun <reified T> getPlayers(clazz: Class<T>) = players.filter { it.value::class.java == clazz }

    fun register(id: String, gui: IGuiScreen) {
        guis[id] = gui
    }

    @EventHandler
    fun click(e: InventoryClickEvent) {
        println((e.whoClicked as Player).getOpenGui()?.javaClass?.simpleName)
        (e.whoClicked as Player).getOpenGui()?.let {
            e.isCancelled = true
            it.click(e)
        }
    }

    @EventHandler
    fun drag(e: InventoryDragEvent) {
        (e.whoClicked as Player).getOpenGui()?.let {
            e.isCancelled = true
            it.drag(e)
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
    fun move(e: PlayerMoveEvent) {
        e.player.getOpenGui()?.move(e)
    }
}