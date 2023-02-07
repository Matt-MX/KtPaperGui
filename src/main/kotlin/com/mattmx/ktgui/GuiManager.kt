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
        Bukkit.getScheduler().runTaskAsynchronously(plugin) { ->
            GitUpdateChecker("https://api.github.com/repos/Matt-MX/KtBukkitGui/releases/latest", KotlinBukkitGui.version,
                { outdated, latest ->
                    if (outdated) {
                        if (KotlinBukkitGui.plugin == null) {
                            plugin.logger.info("${plugin.description.name} is running an outdated version of KtGui (latest v$latest)")
                            plugin.logger.info("New Version https://github.com/Matt-MX/KtBukkitGui/")
                        } else {
                            plugin.logger.info("Running an outdated version (v${KotlinBukkitGui.version}) Latest available (v$latest)")
                            plugin.logger.info("Download here: https://github.com/Matt-MX/KtBukkitGui/releases/latest")
                        }
                    } else plugin.logger.info("Running latest version! (v${KotlinBukkitGui.version})")
                }, { e ->
                    plugin.logger.info("Unable to check for latest version.")
                    e.printStackTrace()
                })
        }
    }

    fun getPlayers(gui: IGuiScreen) = players.filter { it.value == gui }.keys
    inline fun <reified T> getPlayers(clazz: Class<T>) = players.filter { it.value::class.java == clazz }

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