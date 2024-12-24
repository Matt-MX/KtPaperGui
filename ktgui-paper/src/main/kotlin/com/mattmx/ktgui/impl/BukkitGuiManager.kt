package com.mattmx.ktgui.impl

import com.mattmx.ktgui.GuiManager
import com.mattmx.ktgui.TaskWrapper
import com.mattmx.ktgui.screen.GuiType
import com.mattmx.ktgui.tasks.*
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

class BukkitGuiManager(
    private val plugin: JavaPlugin
) : GuiManager<Player, PaperGuiButton<*>, PaperGuiScreen<*>>() {
    private val paperTaskProvider = PaperTaskProviderImpl(plugin) {}

    override fun forcefullyClose(player: Any) {
        val finalPlayer = when (player) {
            is UUID -> Bukkit.getPlayer(player)
            is Player -> player
            else -> error("Unknown player type ${player::class.java.simpleName}!")
        } ?: return

        removeActiveGui(finalPlayer)?.let { gui ->
            gui.close.apply(finalPlayer)
            finalPlayer.closeInventory(InventoryCloseEvent.Reason.PLUGIN)
        }
    }

    override fun createPlatformButtonOfType(typeKeyed: Key): PaperGuiButton<*> {
        val itemType = Material.entries.firstOrNull { material -> material.key() == typeKeyed }
            ?: error("No type known for $typeKeyed!")
        return PaperGuiButton(itemType)
    }

    override fun createPlatformButton(type: Any): PaperGuiButton<*> {
        val itemType = type as? Material ?: error("Type must be a Material!")
        return PaperGuiButton(itemType)
    }

    override fun createPlatformGui(title: Component, type: GuiType): PaperGuiScreen<*> {
        return PaperGuiScreen(type, title)
    }

    override fun getTaskProvider(): TaskProvider<*> {
        return paperTaskProvider
    }

    override fun <T : TaskWrapper> createTaskTracker(plugin: Any): TaskTracker<T> {
        // TODO remove types here its messy
        return PaperTaskTrackerImpl(plugin as JavaPlugin) as TaskTracker<T>
    }

    override fun <T : TaskWrapper> createKeyedTaskTracker(plugin: Any): KeyedTaskTracker<T> {
        return PaperKeyedTaskTrackerImpl(plugin as JavaPlugin) as KeyedTaskTracker<T>
    }
}