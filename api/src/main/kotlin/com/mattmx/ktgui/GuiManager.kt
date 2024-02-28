package com.mattmx.ktgui

import com.mattmx.ktgui.components.screen.IGuiScreen
import com.mattmx.ktgui.configuration.Configuration
import com.mattmx.ktgui.extensions.getOpenGui
import com.mattmx.ktgui.scheduling.Scheduling
import com.mattmx.ktgui.scheduling.TaskTracker
import com.mattmx.ktgui.scheduling.TaskTrackerTask
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.server.PluginDisableEvent
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.annotations.ApiStatus
import java.util.Collections

/**
 * Handles all GUI click events, as well
 * as events we might need to know while in a GUI.
 */
object GuiManager : Listener {
    private val players = Collections.synchronizedMap(hashMapOf<Player, IGuiScreen>())
    private var initialized = false
    private val defaultConfiguration = Configuration()
    private val configurations = hashMapOf<JavaPlugin, Configuration>()
    lateinit var owningPlugin: JavaPlugin

    fun init(plugin: JavaPlugin): Boolean {
        if (initialized) return false
        initialized = true
        owningPlugin = plugin
        Scheduling.plugin = plugin
        Bukkit.getPluginManager().registerEvents(this, plugin)
        return true
    }

    /**
     * Should be called on your plugin's [onDisable] method.
     * Closes all GUIs for all players.
     */
    fun shutdown() {
        Bukkit.getOnlinePlayers().forEach { player -> forceClose(player) }
    }

    /**
     * Each plugin can configure generic things like feedback messages,
     * that KTBukkitGui handles under the hud.
     *
     * @param plugin the plugin for this configuration
     * @param block configuration modification
     */
    @ApiStatus.Experimental
    fun configure(plugin: JavaPlugin, block: Configuration.() -> Unit) {
        val configuration = Configuration()
        block(configuration)
        configurations[plugin] = configuration
    }

    @ApiStatus.Experimental
    fun getConfiguration(plugin: JavaPlugin) = configurations[plugin] ?: defaultConfiguration

    fun getPlayers(gui: IGuiScreen) = players.filter { it.value == gui }.keys
    fun getPlayersInGui() = players.toMutableMap()
    fun getPlayer(player: Player) = players[player]
    fun setOpenGui(player: Player, gui: IGuiScreen) = players.set(player, gui)
    fun clearGui(player: Player) = players.remove(player)
    inline fun <reified T> getPlayers(clazz: Class<T>) = getPlayersInGui().filter { it.value::class.java == clazz }

    @EventHandler
    fun onDisable(e: PluginDisableEvent) {
        Bukkit.getOnlinePlayers().forEach { player -> forceClose(player) }
    }

    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        (e.whoClicked as Player).getOpenGui()?.let {
            e.isCancelled = true
            it.click(e)
        }
    }

    @EventHandler
    fun onInventoryDrag(e: InventoryDragEvent) {
        (e.whoClicked as Player).getOpenGui()?.let {
            e.isCancelled = true
            it.drag(e)
        }
    }

    @EventHandler
    fun onInventoryClose(e: InventoryCloseEvent) {
        val gui = (e.player as Player).getOpenGui()
        gui?.let {
            gui.close(e)
            players.remove(e.player)
        }
    }

    @EventHandler
    fun onPlayerQuit(e: PlayerQuitEvent) {
        val gui = e.player.getOpenGui()
        gui?.let {
            gui.quit(e)
            if (getPlayers(gui).isEmpty()) {
                gui.destroy()
            }
            players.remove(e.player)
        }
    }

    @EventHandler
    fun onPlayerMove(e: PlayerMoveEvent) {
        e.player.getOpenGui()?.move(e)
    }

    /**
     * Call this to close a Player's GUI.
     * It is more safe than using [Player#closeInventory()]
     * since that doesn't call the [InventoryCloseEvent] event.
     *
     * @param player
     */
    fun forceClose(player: Player) {
        val gui = player.getOpenGui()
        gui?.let {
            if (getPlayers(gui).isEmpty()) {
                gui.destroy()
            }
            players.remove(player)
            player.openInventory.close()
        }
    }

    fun isInitialized() = initialized
}