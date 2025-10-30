package com.mattmx.ktgui.impl

import com.mattmx.ktgui.TaskWrapper
import com.mattmx.ktgui.tasks.*
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.player.ServerConnectedEvent
import com.velocitypowered.api.proxy.ProxyServer

class VelocityKtGuiImpl(
    private val plugin: Any,
    private val proxy: ProxyServer
) : PacketEventsKtGui() {
    private val velocityTaskProvider = VelocityTaskProvider(plugin, proxy) {}

    init {
        setInstance(this)
    }

    @Subscribe
    fun onPlayerQuit(event: DisconnectEvent) {
        inventoryTracker.remove(event.player)
        playerLocationTracker.ifPresent { it.remove(event.player) }
        removeActiveGui(event.player)
            ?.close
            ?.apply(event.player)
    }

    @Subscribe
    fun onPlayerChangeServer(event: ServerConnectedEvent) {
        if (event.previousServer.isPresent) {
            // The player is changing server, so we should remove our inventory cache
            inventoryTracker.remove(event.player)
            playerLocationTracker.ifPresent { it.remove(event.player) }
        }
        // TODO(matt): reopen gui when they switch servers??
    }

    override fun registerListeners() {
        super.registerListeners()

        proxy.eventManager.register(plugin, this)
    }

    override fun unregisterListeners() {
        super.unregisterListeners()

        proxy.eventManager.unregisterListener(plugin, this)
    }

    override fun getTaskProvider(): TaskProvider<*> {
        return this.velocityTaskProvider
    }

    override fun <T : TaskWrapper> createTaskTracker(plugin: Any): TaskTracker<T> {
        return VelocityTaskTrackerImpl(plugin, proxy) as TaskTracker<T>
    }

    override fun <T : TaskWrapper> createKeyedTaskTracker(plugin: Any): KeyedTaskTracker<T> {
        return VelocityKeyedTaskTrackerImpl(plugin, proxy) as KeyedTaskTracker<T>
    }
}