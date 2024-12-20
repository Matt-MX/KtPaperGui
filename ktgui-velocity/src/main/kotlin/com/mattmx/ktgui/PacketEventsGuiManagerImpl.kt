package com.mattmx.ktgui

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerCloseWindow
import com.mattmx.ktgui.tasks.KeyedTaskTracker
import com.mattmx.ktgui.tasks.TaskTracker
import com.mattmx.ktgui.tasks.VelocityKeyedTaskTrackerImpl
import com.mattmx.ktgui.tasks.VelocityTaskTrackerImpl
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.player.ServerConnectedEvent
import com.velocitypowered.api.proxy.ProxyServer
import java.time.Duration

class PacketEventsGuiManagerImpl(
    private val plugin: Any,
    private val proxy: ProxyServer
) : PacketEventsGuiManager() {

    init {
        setInstance(this)
    }

    @Subscribe
    fun onPlayerQuit(event: DisconnectEvent) {
        inventoryTracker.remove(event.player)
        playerLocationTracker.ifPresent { it.remove(event.player) }
        removeActiveGui(event.player)
    }

    @Subscribe
    fun onPlayerChangeServer(event: ServerConnectedEvent) {
        if (event.previousServer.isPresent) {
            // The player is changing server, so we should remove our inventory cache
            inventoryTracker.remove(event.player)
            playerLocationTracker.ifPresent { it.remove(event.player) }
        }
    }

    override fun registerListeners() {
        super.registerListeners()

        proxy.eventManager.register(plugin, this)
    }

    override fun unregisterListeners() {
        super.unregisterListeners()

        proxy.eventManager.unregisterListener(plugin, this)
    }

    override fun forcefullyClose(player: Any) {
        val gui = removeActiveGui(player) ?: return

        gui.close.apply(player)

        val packet = WrapperPlayServerCloseWindow(gui.windowId)

        PacketEvents.getAPI()
            .playerManager
            .sendPacket(player, packet)
    }

    override fun createRepeatingTask(repeat: Duration, task: () -> Unit): TaskWrapper {

        val scheduledTask = proxy.scheduler
            .buildTask(plugin, task)
            .repeat(repeat)
            .delay(Duration.ZERO)
            .schedule()

        return TaskWrapper { scheduledTask.cancel() }
    }

    override fun <T, D : Any>  createTaskTracker(plugin: Any): TaskTracker<T, D> {
        return VelocityTaskTrackerImpl(plugin, proxy) as TaskTracker<T, D>
    }

    override fun <T, D : Any>  createKeyedTaskTracker(plugin: Any): KeyedTaskTracker<T, D> {
        return VelocityKeyedTaskTrackerImpl(plugin, proxy) as KeyedTaskTracker<T, D>
    }
}