package com.mattmx.ktgui.impl

import com.mattmx.ktgui.TaskWrapper
import com.mattmx.ktgui.listener.InventoryTracker
import com.mattmx.ktgui.listener.PlayerLocationTracker
import com.mattmx.ktgui.tasks.*
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

class PaperGuiManagerImpl(
    private val plugin: JavaPlugin
) : PacketEventsGuiManager(), Listener {
    private val paperTaskProvider = PaperTaskProviderImpl(plugin) {}
    override var playerLocationTracker: Optional<PlayerLocationTracker> = Optional.of(DummyPlayerLocationTracker())

    init {
        setInstance(this)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        removeActiveGui(event.player)
            ?.close
            ?.apply(event.player)
    }

    override fun getTaskProvider(): TaskProvider<*> {
        return paperTaskProvider
    }

    override fun registerListeners() {
        super.registerListeners()

        Bukkit.getPluginManager().registerEvents(this, plugin)
    }

    override fun unregisterListeners() {
        super.unregisterListeners()

        HandlerList.unregisterAll(this)
    }

    override fun <T : TaskWrapper> createTaskTracker(plugin: Any): TaskTracker<T> {
        return PaperTaskTrackerImpl(plugin as JavaPlugin) as TaskTracker<T>
    }

    override fun <T : TaskWrapper> createKeyedTaskTracker(plugin: Any): KeyedTaskTracker<T> {
        return PaperKeyedTaskTrackerImpl(plugin as JavaPlugin) as KeyedTaskTracker<T>
    }
}