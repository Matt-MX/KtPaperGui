package com.mattmx.ktgui

import com.mattmx.ktgui.commands.wrapper.CommandWrapper
import com.mattmx.ktgui.components.screen.IGuiScreen
import com.mattmx.ktgui.cooldown.ActionCoolDown
import com.mattmx.ktgui.dsl.event
import com.mattmx.ktgui.extensions.getOpenGui
import com.mattmx.ktgui.guiconfig.GuiConfigManager
import com.mattmx.ktgui.scheduling.Scheduling
import com.mattmx.ktgui.utils.InstancePackageClassCache
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandMap
import org.bukkit.command.SimpleCommandMap
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
import java.util.*

/**
 * Handles all GUI click events, as well
 * as events we might need to know while in a GUI.
 */
object GuiManager : Listener {
    private val players = Collections.synchronizedMap(hashMapOf<Player, IGuiScreen>())
    private var initialized = false
    private val pluginCache = InstancePackageClassCache<JavaPlugin>()
    val guiConfigManager = GuiConfigManager()
    lateinit var owningPlugin: JavaPlugin

    fun init(plugin: JavaPlugin): Boolean {
        if (initialized) {
            pluginCache.cacheInstance(plugin::class.java, plugin)
            return false
        }
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

    fun registerCommand(classOrPlugin: Class<*>, command: CommandWrapper) {
        val existingCommand = Bukkit.getPluginCommand(command.name)
        if (existingCommand != null) {
            Bukkit.getPluginCommand(command.name)?.setExecutor(command)
        } else {
            if (!isInitialized()) {
                throw RuntimeException("Unregistered commands are unsupported when GuiManager not initialised! Call GuiManager.init")
            }

            var cmdPlugin = pluginCache.getInstanceOrNull(classOrPlugin)

            val prefix = if (cmdPlugin == null) {
                cmdPlugin = owningPlugin

                cmdPlugin.logger.warning("Unable to find owning plugin for class ${classOrPlugin.simpleName} when registering command '${command.name}'.")
                cmdPlugin.name.lowercase()
            } else cmdPlugin.name.lowercase()

            val cmdMapField = Bukkit.getServer().javaClass.getDeclaredField("commandMap")
            cmdMapField.isAccessible = true

            val cmdMap = cmdMapField.get(Bukkit.getServer()) as CommandMap
            cmdMap.register(prefix, command)

            val knownCommandsField = SimpleCommandMap::class.java.getDeclaredField("knownCommands")
            knownCommandsField.isAccessible = true

            val knownCommands = knownCommandsField.get(cmdMap) as MutableMap<String?, Command?>
            var knownAliases: Set<String?>? = null

            try {
                val aliasesField = SimpleCommandMap::class.java.getDeclaredField("aliases")
                aliasesField.setAccessible(true)
                knownAliases = aliasesField.get(cmdMap) as MutableSet<String?>
            } catch (e: NoSuchFieldException) {
            }

            event<PluginDisableEvent>(cmdPlugin) {
                if (this.plugin == cmdPlugin) {
                    synchronized(cmdMap) {
                        knownCommands.remove(command.name)
                        knownCommands.remove("$prefix:${command.name}")
                        knownAliases?.minus(command.aliases.toSet())
                        for (alias in command.aliases) {
                            knownCommands.remove(alias)
                            knownCommands.remove("$prefix:$alias")
                        }
                        command.unregister(cmdMap)
                        command.aliases = listOf()
                    }
                    cmdPlugin.logger.info("Unregistered ${command.name}")
                }
            }
        }
    }

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

        ActionCoolDown.removeUsers(e.player, e.player.uniqueId, e.player.name)
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